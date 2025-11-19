package com.gcgenome.lims.analysis.actor.hrd

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.analysis.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

@Service
class HrdQc(
    analysisManager: AnalysisManager,
    mutex: Mutex,
    @Value("\${subscriber.hrd.sheet}") val sheet: UUID,
    om: ObjectMapper
): Hrd, AbstractQcFileReader(analysisManager, mutex, sheet, om, "\t") {
    private val fileNameMatcher = "(^\\d{2}OncoHRD\\d{3})[-_]QC.txt".toRegex()
    private val rowHeaderMatcher = "(^\\d{2}OncoHRD\\d{3})[-_](\\d{2}HRD\\d{3,4})[-_](\\d{8}-\\d{3}-\\d{4})[-_]([a-zA-Z]\\d{1,2})".toRegex()
    override fun chkFormat(file: Path): Boolean = fileNameMatcher.matches(file.name)
    override fun batch(path: Path): String = fileNameMatcher.find(path.name)!!.groupValues[1]
    override fun header(header: String): QcRowHeader? {
        val match = rowHeaderMatcher.find(header) ?: return null
        return QcRowHeader(
            serial = match.groupValues[0],
            batch = match.groupValues[1],
            panel = "HRD",
            subpanel = "HRD",
            sample = match.groupValues[3].replace("-", "").toLong(),
            row = match.groupValues[4].replace(Regex("[^0-9]"), "").toInt()
        )
    }
}
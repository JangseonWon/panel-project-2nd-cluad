package com.gcgenome.lims.analysis.actor.all.v2

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.analysis.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

@Service("allQcV2")
class AllQc(
    analysisManager: AnalysisManager,
    mutex: Mutex,
    @Value("\${subscriber.all.sheet}") val sheet: UUID,
    om: ObjectMapper
): All, AbstractQcFileReader(analysisManager, mutex, sheet, om) {
    private val fileNameMatcher = "^(\\d{2}HEMA\\d{1,4})[-_](ALL|ALLB|ALLF)[-_]QC\\.csv$".toRegex()
    private val rowHeaderMatcher = "^((\\d{2}HEMA\\d{1,4})[-_](ALL|ALLB|ALLF)[-_](\\d{1,4})[-_](\\d{8}-\\d{3}-\\d{4})).*".toRegex()
    override fun chkFormat(file: Path): Boolean = fileNameMatcher.matches(file.name)
    override fun batch(path: Path): String = fileNameMatcher.find(path.name)!!.groupValues[1]
    override fun header(header: String): QcRowHeader? {
        val match = rowHeaderMatcher.find(header) ?: return null
        return QcRowHeader(
            serial = match.groupValues[1],
            batch = match.groupValues[2],
            panel = "HEMA",
            subpanel = "ALL",
            sample = match.groupValues[5].replace("-", "").toLong(),
            row = match.groupValues[4].toInt()
        )
    }
}
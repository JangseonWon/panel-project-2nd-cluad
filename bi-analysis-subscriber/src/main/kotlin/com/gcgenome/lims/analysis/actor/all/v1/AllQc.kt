package com.gcgenome.lims.analysis.actor.all.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.analysis.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

@Service("allQcV1")
class AllQc(
    analysisManager: AnalysisManager,
    mutex: Mutex,
    @Value("\${subscriber.all.sheet}") val sheet: UUID,
    om: ObjectMapper
): All, AbstractQcFileReader(analysisManager, mutex, sheet, om) {
    private val fileNameMatcher = "^(\\d{2}ALL[BF]\\d{1,4})[-_]QC.csv".toRegex()
    private val rowHeaderMatcher = "^I_(\\d{2}(ALL[BF]){1}\\d{1,4})[-_](.*)[-_](?:(\\d{8}-\\d{3}-\\d{4})|(?i)(positive))".toRegex()
    override fun chkFormat(file: Path): Boolean = fileNameMatcher.matches(file.name)
    override fun batch(path: Path): String = fileNameMatcher.find(path.name)!!.groupValues[1]
    override fun header(header: String): QcRowHeader? {
        val match = rowHeaderMatcher.find(header) ?: return null
        return QcRowHeader(
            serial = match.groupValues[0],
            batch = match.groupValues[1],
            panel = "ALL",
            subpanel = match.groupValues[2],
            sample = match.groupValues[4].replace("-", "").toLong(),
            row = match.groupValues[3].toInt(),
        )
    }
}
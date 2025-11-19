package com.gcgenome.lims.analysis.actor.tso

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.analysis.AbstractQcFileReader
import com.gcgenome.lims.analysis.AnalysisManager
import com.gcgenome.lims.analysis.Mutex
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

@Service
class TsoQc(
    analysisManager: AnalysisManager,
    mutex: Mutex,
    @Value("\${subscriber.tso.sheet}") val sheet: UUID,
    om: ObjectMapper
): Tso, AbstractQcFileReader(analysisManager, mutex, sheet, om, "\t") {
    private val fileNameMatcher = "^(\\d{2}[A-Z]{2}TSO\\d{1,4}).stat.txt".toRegex()
    private val rowHeaderMatcher = "^(\\d{2}([A-Z]{2}TSO)\\d{1,4})[-_](\\d+)[-_](\\d{8}-\\d{3}-\\d{4})[-_]([A-Z]{1,4})[-_]".toRegex()
    override fun chkFormat(file: Path): Boolean = fileNameMatcher.matches(file.name)
    override fun batch(path: Path): String = fileNameMatcher.find(path.name)!!.groupValues[1]
    override fun header(header: String): QcRowHeader? {
        val match = rowHeaderMatcher.find(header) ?: return null
        return QcRowHeader(
            serial = match.groupValues[0],
            batch = match.groupValues[1],
            panel = match.groupValues[2],
            subpanel = match.groupValues[5],
            sample = match.groupValues[4].replace("-", "").toLong(),
            row = match.groupValues[3].toInt(),
        )
    }
}
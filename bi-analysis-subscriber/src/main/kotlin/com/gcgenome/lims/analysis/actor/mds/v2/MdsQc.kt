package com.gcgenome.lims.analysis.actor.mds.v2

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.analysis.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

@Service("mdsQcV2")
class MdsQc(
    analysisManager: AnalysisManager,
    mutex: Mutex,
    @Value("\${subscriber.mds.sheet}") val sheet: UUID,
    om: ObjectMapper
): Mds, AbstractQcFileReader(analysisManager, mutex, sheet, om) {
    private val fileNameMatcher = "^(\\d{2}HEMA\\d{1,4})[-_]MDS[-_]QC\\.csv$".toRegex()
    private val rowHeaderMatcher = "^((\\d{2}HEMA\\d{1,4})[-_](MDS)[-_](\\d{1,4})[-_](\\d{8}-\\d{3}-\\d{4})).*".toRegex()
    override fun chkFormat(file: Path): Boolean = fileNameMatcher.matches(file.name)
    override fun batch(path: Path): String = fileNameMatcher.find(path.name)!!.groupValues[1]
    override fun header(header: String): QcRowHeader? {
        val match = rowHeaderMatcher.find(header) ?: return null
        return QcRowHeader(
            serial  = match.groupValues[1],
            batch   = match.groupValues[2],
            panel   = "HEMA",
            subpanel= "MDS",
            sample  = match.groupValues[5].replace("-", "").toLong(),
            row     = match.groupValues[4].toInt()
        )
    }
}
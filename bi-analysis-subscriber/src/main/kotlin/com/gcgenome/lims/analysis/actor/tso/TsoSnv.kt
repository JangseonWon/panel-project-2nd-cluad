package com.gcgenome.lims.analysis.actor.tso

import com.gcgenome.lims.analysis.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

@Service
class TsoSnv(
    analysisManager: AnalysisManager,
    snvManager: SnvManager,
    @Value("\${subscriber.tso.sheet}") val sheet: UUID,
    mutex: Mutex
): Tso, AbstractSnvFileReader(analysisManager, snvManager, mutex, sheet) {
    override fun chkFormat(file: Path): Boolean = TsoFileFormatter.match(file.name)!=null
    override fun format(path: Path): SnvFileFormat? = TsoFileFormatter.match(path.name)
    override fun batch(path: Path): String? = TsoFileFormatter.match(path.name)?.batch
    object TsoFileFormatter {
        private val matcher = "^(\\d{2}([A-Z]{2}TSO)\\d{1,4})[-_](\\d+)[-_](\\d{8}-\\d{3}-\\d{4})[-_]([A-Z]{1,4})[-_]".toRegex()
        fun match(fileName: String): SnvFileFormat? {
            val match = matcher.find(fileName) ?: return null
            return SnvFileFormat(
                serial  = match.groupValues[0],
                batch   = match.groupValues[1],
                panel   = match.groupValues[2],
                subpanel= match.groupValues[5],
                sample  = match.groupValues[4].replace("-", "").toLong(),
                row     = match.groupValues[3].toInt(),
                tag     = tag(fileName)
            )
        }
        private fun tag(fileName: String): String = when {
            fileName.lowercase().endsWith("annotation_filter_candidates.txt") -> "candidate"
            fileName.lowercase().endsWith("annotation_filter.txt") -> "all"
            else -> ""
        }
    }
}
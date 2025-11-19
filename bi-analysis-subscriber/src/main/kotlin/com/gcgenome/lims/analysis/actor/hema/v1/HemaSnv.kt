package com.gcgenome.lims.analysis.actor.hema.v1

import com.gcgenome.lims.analysis.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

@Service("hemaSnvV1")
class HemaSnv(
    analysisManager: AnalysisManager,
    snvManager: SnvManager,
    @Value("\${subscriber.hema.sheet}") val sheet: UUID,
    mutex: Mutex
): Hema, AbstractSnvFileReader(analysisManager, snvManager, mutex, sheet) {
    override fun chkFormat(file: Path): Boolean = HemaFileFormatter.match(file.name) !=null
    override fun format(path: Path): SnvFileFormat? = HemaFileFormatter.match(path.name)
    override fun batch(path: Path): String? = HemaFileFormatter.match(path.name)?.batch
    object HemaFileFormatter {
        private val matcher = "^I_(\\d{2}HEMA\\d{1,4})[-_](.*)[-_](\\d{1,4})[-_](?:(\\d{8}-\\d{3}-\\d{4})|(?i)(positive))".toRegex()
        fun match(fileName: String): SnvFileFormat? {
            val match = matcher.find(fileName) ?: return null
            return SnvFileFormat(
                serial  = match.groupValues[0],
                batch   = match.groupValues[1],
                panel   = "HEMA",
                subpanel= match.groupValues[2],
                sample  = match.groupValues[4].replace("-", "").toLong(),
                row     = match.groupValues[3].toInt(),
                tag     = tag(fileName)
            )
        }
        private fun tag(fileName: String): String = when {
            fileName.lowercase().endsWith("annotation_filter_candidates.txt") -> "candidate"
            fileName.lowercase().endsWith("annotation_filter.txt") -> "all"
            fileName.lowercase().endsWith("pindel.annotation.txt") -> "pindel"
            else -> ""
        }
    }
}
package com.gcgenome.lims.analysis.actor.lym.v1

import com.gcgenome.lims.analysis.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

@Service("lymSnvV1")
class LymSnv(
    analysisManager: AnalysisManager,
    snvManager: SnvManager,
    @Value("\${subscriber.lym.sheet}") val sheet: UUID,
    mutex: Mutex
): Lym, AbstractSnvFileReader(analysisManager, snvManager, mutex, sheet) {
    override fun chkFormat(file: Path): Boolean = LymFileFormatter.match(file.name)!=null
    override fun format(path: Path): SnvFileFormat? = LymFileFormatter.match(path.name)
    override fun batch(path: Path): String? = LymFileFormatter.match(path.name)?.batch
    object LymFileFormatter {
        private val matcher = "^I_(\\d{2}(LYM[BF]){1}\\d{1,4})[-_](.*)[-_](?:(\\d{8}-\\d{3}-\\d{4})|(?i)(positive))".toRegex()
        fun match(fileName: String): SnvFileFormat? {
            val match = matcher.find(fileName) ?: return null
            return SnvFileFormat(
                serial  = match.groupValues[0],
                batch   = match.groupValues[1],
                panel   = "LYM",
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
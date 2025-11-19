package com.gcgenome.lims.analysis.actor.all.v2

import com.gcgenome.lims.analysis.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

@Service("allSnvV2")
class AllSnv(
    analysisManager: AnalysisManager,
    snvManager: SnvManager,
    @Value("\${subscriber.all.sheet}") val sheet: UUID,
    mutex: Mutex
): All, AbstractSnvFileReader(analysisManager, snvManager, mutex, sheet) {
    override fun chkFormat(file: Path): Boolean = AllFileFormatter.match(file.name) !=null
    override fun format(path: Path): SnvFileFormat? = AllFileFormatter.match(path.name)
    override fun batch(path: Path): String? = AllFileFormatter.match(path.name)?.batch
    object AllFileFormatter {
        private val matcher = "^((\\d{2}HEMA\\d{1,4})[-_](ALL|ALLB|ALLF)[-_](\\d{1,4})[-_](\\d{8}-\\d{3}-\\d{4})).*".toRegex()
        fun match(fileName: String): SnvFileFormat? {
            val match = matcher.find(fileName) ?: return null
            return SnvFileFormat(
                serial  = match.groupValues[1],
                batch   = match.groupValues[2],
                panel   = "HEMA",
                subpanel= "ALL",
                sample  = match.groupValues[5].replace("-", "").toLong(),
                row     = match.groupValues[4].toInt(),
                tag     = tag(fileName)
            )
        }
        private fun tag(fileName: String): String = when {
            fileName.lowercase().endsWith("annotation_outer_filter1.txt") -> "all"
            fileName.lowercase().endsWith("annotation_inter_filter1.txt") -> "single"
            fileName.lowercase().endsWith("annotation_inter_filter2_final.txt") -> "candidate"
            fileName.lowercase().endsWith("pindel.annotation_inter.txt") -> "pindel"
            else -> ""
        }
    }
}
package com.gcgenome.lims.analysis.actor.aml.v2

import com.gcgenome.lims.analysis.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

@Service("amlSnvV2")
class AmlSnv(
    analysisManager: AnalysisManager,
    snvManager: SnvManager,
    @Value("\${subscriber.aml.sheet}") val sheet: UUID,
    mutex: Mutex
): Aml, AbstractSnvFileReader(analysisManager, snvManager, mutex, sheet) {
    override fun chkFormat(file: Path): Boolean = AmlFileFormatter.match(file.name) !=null
    override fun format(path: Path): SnvFileFormat? = AmlFileFormatter.match(path.name)
    override fun batch(path: Path): String? = AmlFileFormatter.match(path.name)?.batch
    object AmlFileFormatter {
        private val matcher = "^((\\d{2}HEMA\\d{1,4})[-_](AML)[-_](\\d{1,4})[-_](\\d{8}-\\d{3}-\\d{4})).*".toRegex()
        fun match(fileName: String): SnvFileFormat? {
            val match = matcher.find(fileName) ?: return null
            return SnvFileFormat(
                serial  = match.groupValues[1],
                batch   = match.groupValues[2],
                panel   = "HEMA",
                subpanel= "AML",
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
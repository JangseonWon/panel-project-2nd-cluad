package com.gcgenome.lims.analysis

import com.gcgenome.lims.analysis.entity.Analysis

object IdGenerator {
    private val PATTERN_CHR = "^(?i)(?:chr)*(\\d+)|(\\w+)$".toRegex()
    private fun chrom(chr: String?): String? {
        if (chr == null || chr.trim().isEmpty()) return null
        val m = PATTERN_CHR.find(chr.trim())
        return if (m!=null) when {
            m.groupValues[2].isNotBlank() -> m.groupValues[2]
            else -> String.format("%02d", m.groupValues[1].toInt())
        } else null
    }
    fun analysisId(analysis: Analysis): String              = "${analysis._id.sheet}:${analysis._id.batch}:${analysis.row.toString().padStart(3, '0')}"
    fun snvId(map: Map<String, String?>): String            = "${map["reference"]}:${chrom(map["chrom"])}:${map["pos"]!!.padStart(9, '0')}:${map["ref"]}:${map["alt"]}"
    fun analysisSnvId(analysis: Analysis, snv: String)      = "${analysisId(analysis)}:$snv"
}
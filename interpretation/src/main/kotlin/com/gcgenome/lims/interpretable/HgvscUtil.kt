package com.gcgenome.lims.interpretable

object HgvscUtil {
    fun trimOrDefault(hgvsc: String?): String? {
        return hgvsc?.trim() ?: "NM_000000.0:c.?"
    }
    fun extractCodingPart(hgvsc: String?): String? {
        return hgvsc?.substringAfter(":")
    }
}
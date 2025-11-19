package com.gcgenome.lims.interpretable

object HgvspUtil {
    fun trimOrDefault(hgvsp: String?): String {
        return hgvsp?.trim() ?: "p.?"
    }
}
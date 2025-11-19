package com.gcgenome.lims.interpretable.variant

import com.gcgenome.lims.constants.Clazz

data class SnvImpl(
    private val gene: String,
    private val hgvsc: String,
    private val hgvsp: String? = null,
    private val originHgvsc: String,
    private val originHgvsp: String? = null,
    private val zygosity: String? = null,
    private val disease: List<Disease> = emptyList(),
    private val clazz: Clazz,
    private val gnomad: String? = null,
    private val krgdb: String? = null,
    private val sift: String? = null,
    private val polyphen: String? = null,
    private val polyphen2: String? = null,
    private val mutationtaster: String? = null,
    private val clinvarId: String? = null,
    private val clinvarClass: String? = null,
    private val hgmdDisease: String? = null,
    private val hgmdPmid: String? = null,
    private val interpretation: String? = null
): Snv {
    override fun gene(): String = gene
    override fun hgvsc(): String  = hgvsc
    override fun hgvsp(): String? = if("p.(?)".contentEquals(hgvsp, true) || hgvsp?.isEmpty() != false) null else String.trim(hgvsp)
    override fun originHgvsc(): String  = originHgvsc
    override fun originHgvsp(): String? = if("p.(?)".contentEquals(originHgvsp, true) || originHgvsp?.isEmpty() != false) null else String.trim(originHgvsp)
    override fun zygosity(): String? = String.trim(zygosity)
    override fun disease(): List<Disease> = disease
    override fun clazz(): String = clazz.simpleName
    override fun gnomad(): String?  = gnomad
    override fun krgdb(): String? = krgdb
    override fun sift(): Char? = String.trim(sift)?.get(0)?.uppercaseChar()
    override fun polyphen(): Char? {
        return when(polyphen) {
            null            -> null
            "benign", "B"   -> 'B'
            "unknown"       -> null
            else            -> 'D'
        }
    }
    override fun polyphen2(): Char? {
        return when(polyphen2) {
            null            -> null
            "benign", "B"   -> 'B'
            "unknown"       -> null
            else            -> 'D'
        }
    }
    override fun mutationtaster(): Char? = String.trim(mutationtaster)?.get(0)?.uppercaseChar()
    override fun clinvarId(): String? = String.trim(clinvarId)
    override fun clinvarClass(): String? = String.trim(clinvarClass)
    override fun hgmdDisease(): String? {
        val fullname = String.trim(hgmdDisease) ?: return null
        return fullname.replace("|", ",")
            .replace("_", " ")
            .replace("?", "")
            .replace("}", "")
            .replace("{", "")

    }
    override fun hgmdPmid(): String? = String.trim(hgmdPmid)
    override fun interpretation(): String? = String.trim(interpretation)

    private fun String.Companion.trim(str: String?): String? {
        if (str.isNullOrEmpty()) return str
        @Suppress("NAME_SHADOWING") val str = str.trim()
        if (str.isEmpty() || ".".contentEquals(str)) return null
        return str
    }
}
package com.gcgenome.lims.interpretable.variant

interface Snv: Variant {
    fun hgvsc(): String
    fun hgvsp(): String?
    fun originHgvsc(): String
    fun originHgvsp(): String?
    fun zygosity(): String?
    fun disease(): List<Disease>
    fun clazz(): String

    fun gnomad(): String?
    fun krgdb(): String?

    fun sift(): Char?
    fun polyphen(): Char?
    fun polyphen2(): Char?
    fun mutationtaster():Char?

    fun clinvarId(): String?
    fun clinvarClass(): String?

    fun hgmdDisease(): String?
    fun hgmdPmid(): String?

    fun interpretation(): String?
}
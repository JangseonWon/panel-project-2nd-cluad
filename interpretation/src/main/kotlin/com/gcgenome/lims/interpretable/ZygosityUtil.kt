package com.gcgenome.lims.interpretable

import com.gcgenome.lims.interpretable.impl.Sex

object ZygosityUtil {
    private val ZYGOSITY_REGEX = "^(\\d)[/|](\\d)$".toRegex()
    fun normalize(zygosity: String?, sex: Sex, chr: String) = when {
        zygosity == null -> ""
        "Het".equals(zygosity, ignoreCase = true)               ->      "Heterozygous"
        "Hom".equals(zygosity, ignoreCase = true)               ->      "Homozygous"
        "Hem".equals(zygosity, ignoreCase = true)               ->      "Hemizygous"
        "Heterozygous".equals(zygosity, ignoreCase = true)      ->      "Heterozygous"
        "Homozygous".equals(zygosity, ignoreCase = true)        ->      "Homozygous"
        "Hemizygous".equals(zygosity, ignoreCase = true)        ->      "Hemizygous"

        else -> {
            val match = ZYGOSITY_REGEX.find(zygosity)
            if(match == null) ""
            else {
                val n = match.groupValues.stream().skip(1).mapToInt(String::toInt).sum()
                check(n in 0..2)
                if (n == 2) {
                    if (sex == Sex.M && chr.uppercase().contains("X")) "Hemizygous"
                    else "Homozygous"
                } else if (n == 1) {
                    if (sex == Sex.M && chr.uppercase().contains("X")) "Hemizygous"
                    else "Heterozygous"
                } else "Homozygous"
            }
        }
    }
    fun toShort(full: String): String = when (full) {
        "Homozygous"    -> "Hom"
        "Heterozygous"  -> "Het"
        "Hemizygous"    -> "Hem"
        else            -> full
    }
    fun abbreviation(zygosity: String): String = "${toShort(zygosity)}, $zygosity"
}
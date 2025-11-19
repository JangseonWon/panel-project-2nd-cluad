package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.interpretable.PopulationPhrase
import com.gcgenome.lims.interpretable.variant.Snv

internal class PopulationPhraseKoKr: PopulationPhrase {
    override fun population(variant: Snv, hasMaf:Boolean): String {
        val gnomad = variant.gnomad()
        val krgdb = variant.krgdb()
        val maf = if(hasMaf) "MAF" else "minor allele frequency (MAF)"
        return if(gnomad.isNullOrZero().not() && krgdb.isNullOrZero().not()) String.format("전체 인구 집단(gnomAD)에서의 ${maf}가 %s%%이며 한국인 인구 집단(KRGDB)에서는 %s%%인 ", gnomad, krgdb)
        else if(gnomad.isNullOrZero().not()) String.format("전체 인구 집단(gnomAD)에서의 ${maf}가 %s%%이며 한국인 인구 집단(KRGDB)에서는 보고된 바 없는 ", gnomad)
        else if(krgdb.isNullOrZero().not()) String.format("전체 인구 집단(gnomAD)에서는 보고된 바 없으나 한국인 인구 집단(KRGDB)에서의 ${maf}는 %s%%인 ", krgdb)
        else "일반 인구 집단(gnomAD, KRGDB)에서 보고된 바 없는 매우 드문 "
    }
    private fun String?.isNullOrZero(): Boolean = this==null || this.toDouble() <= 0
}
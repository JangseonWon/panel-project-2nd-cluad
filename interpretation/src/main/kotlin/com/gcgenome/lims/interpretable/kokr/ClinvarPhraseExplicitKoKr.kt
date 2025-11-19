package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.interpretable.ClinvarPhrase
import com.gcgenome.lims.interpretable.variant.Snv

internal class ClinvarPhraseExplicitKoKr: ClinvarPhrase {
    override fun clinvar(variant: Snv): String? {
        if(variant.clinvarClass()==null) return null
        var clinvar = variant.clinvarClass()!!
            .replace("Uncertain_significance", "Uncertain significance")
            .replace("Likely_pathogenic", "Likely Pathogenic")
            .replace("Likely_Pathogenic", "Likely Pathogenic")
            .replace("Pathogenic", "Pathogenic")
            .replace("Likely_benign", "Likely Benign")
            .replace("Likely_Benign", "Likely Benign")
        if (clinvar.contains("Conflicting", true)) {
            clinvar = clinvar.substring(clinvar.indexOf('|') + 1)
            clinvar = clinvar.replace("&", " 또는 ")
        }
        clinvar = clinvar.replace("_", " ")
        while(clinvar.contains("  ")) clinvar = clinvar.replace("  ", " ")
        return "ClinVar에서 ${clinvar}${Korean.class한글조사(clinvar)}로 분류되어 있습니다(ID: ${variant.clinvarId()})"
    }
    private fun Korean.Companion.class한글조사(clazz: String): String =
        if (clazz.endsWith("ce")) "으" else ""
}
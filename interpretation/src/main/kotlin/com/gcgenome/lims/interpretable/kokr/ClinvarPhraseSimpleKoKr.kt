package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.interpretable.ClinvarPhrase
import com.gcgenome.lims.interpretable.variant.Snv

internal class ClinvarPhraseSimpleKoKr: ClinvarPhrase {
    override fun clinvar(variant: Snv): String? {
        if(variant.clinvarClass()==null) return null
        var clinvar = variant.clinvarClass()!!
            .replace("Uncertain_significance", "VUS")
            .replace("Likely_pathogenic", "LPV")
            .replace("Likely_Pathogenic", "LPV")
            .replace("Pathogenic", "PV")
            .replace("Likely_benign", "LBV")
            .replace("Likely_Benign", "LBV")
            .replace("Benign", "BV")
        if (clinvar.contains("Conflicting", true)) {
            clinvar = clinvar.substring(clinvar.indexOf('|') + 1)
            clinvar = clinvar.replace("&", " 또는 ")
        }
        clinvar = clinvar.replace("_", " ")
        while(clinvar.contains("  ")) clinvar = clinvar.replace("  ", " ")
        return "ClinVar에서 ${clinvar}로 분류되어 있습니다(ID: ${variant.clinvarId()})"
    }
}
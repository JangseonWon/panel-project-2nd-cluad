package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.interpretable.ClinvarPhrase
import com.gcgenome.lims.interpretable.variant.Snv

internal class ClinvarPhraseVerboseKoKr: ClinvarPhrase {
    private val CLINVAR_IGNORES = arrayOf(
        ",_other", ",_risk_factor", ",_protective", "not_provided"
    )
    override fun clinvar(variant: Snv): String? {
        var clinvar: String? = variant.clinvarClass() ?: return null
        for (ignore in CLINVAR_IGNORES) clinvar = clinvar!!.replace(ignore, "")
        if (clinvar!!.isEmpty() || ".".equals(clinvar, ignoreCase = true)) return null
        return if (clinvar.startsWith("Conflicting_interpretations_of_pathogenicity"))
            "Clinvar에서 Submitter마다 서로 상이한 결과를 보였습니다[${clinvar.substring(clinvar.indexOf("|") + 1).replace('_', ' ')}]."
        else "Clinvar에서 ${clinvar.replace('_', ' ')}${if (clinvar.endsWith("cance")) "" else "으"}로 분류되어 있습니다(ID: ${variant.clinvarId()})."
    }
}
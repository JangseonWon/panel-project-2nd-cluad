package com.gcgenome.lims.interpretable

import com.gcgenome.lims.interpretable.variant.Snv

interface ClinvarPhrase {
    fun clinvar(variant: Snv): String?
}
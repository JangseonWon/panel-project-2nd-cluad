package com.gcgenome.lims.interpretable

import com.gcgenome.lims.interpretable.variant.Snv

interface InsilicoPhrase {
    fun insilico(variant: Snv): String?
}
package com.gcgenome.lims.interpretable

import com.gcgenome.lims.interpretable.variant.Snv

interface PopulationPhrase {
    fun population(variant: Snv, hasMaf:Boolean): String?
}
package com.gcgenome.lims.dto

import com.fasterxml.jackson.annotation.JsonUnwrapped

data class InterpretationPanelMlpa(@JsonUnwrapped val panel: InterpretationPanel, val mlpaResult: Mlpa) {
    data class Mlpa (
        val gene: String,
        val result: String,
        val exons: String?,
        val zygosity: String?,
        val delDup: String?
    )
}
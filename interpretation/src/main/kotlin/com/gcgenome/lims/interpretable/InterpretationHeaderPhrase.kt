package com.gcgenome.lims.interpretable

import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.dto.InterpretationPanel

interface InterpretationHeaderPhrase {
    fun result(variants: List<Pair<InterpretationPanel.Variant, List<Disease>>>): String
}
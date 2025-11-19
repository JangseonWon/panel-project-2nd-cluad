package com.gcgenome.lims.interpretable

import com.gcgenome.lims.dto.InterpretationPanel

object ReferenceUtil: Unwrappable {
    fun abbreviation(variant: InterpretationPanel.Variant): String? {
        requireNotNull(variant.hgvsc) { "hgvsc cannot be null." }
        return if(variant.hgvsc.contains(":")) "${variant.hgvsc.split(":")[0].trim()}(${variant.gene})" else null
    }
}
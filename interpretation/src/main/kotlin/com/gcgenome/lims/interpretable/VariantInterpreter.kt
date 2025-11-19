package com.gcgenome.lims.interpretable

import com.gcgenome.lims.interpretable.variant.Variant

interface VariantInterpreter<V: Variant> {
    fun initialize()
    fun interpret(variant: V): String
}
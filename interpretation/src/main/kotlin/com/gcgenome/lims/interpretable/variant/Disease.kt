package com.gcgenome.lims.interpretable.variant

interface Disease {
    fun fullName(): String
    fun shortName(): String
    fun inheritance(): String
}
package com.gcgenome.lims.interpretable.variant

data class DiseaseImpl(
    private val fullName: String,
    private val shortName: String,
    private val inheritance: String
): Disease {
    override fun fullName(): String = fullName
    override fun shortName(): String = shortName
    override fun inheritance(): String = inheritance
}

package com.gcgenome.lims.dto

data class InterpretationSomatic(
    var cancerType: String,
    var results: List<InterpretationTier>,
    var drugPhenotypes: List<DrugPhenotype>
) {
    data class InterpretationTier(
        val tier: String,
        val variants: List<Variant>,
        val interpretation: String?
    )
    data class Variant(
        val snv: String?,
        val analysis: String?,
        val gene: String,
        val hgvsc: String,
        val hgvsp: String,
        val vaf: Double,
        val depth: Double,
        val cosmic: String?,
        val interpretation: String?
    )
    data class DrugPhenotype(
        val gene: String,
        val diplotype: String,
        val alleleStatus: String,
        val phenotype: String,
        val result: String,
        val interpretation: String
    )
}
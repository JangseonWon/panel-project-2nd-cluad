package com.gcgenome.lims.dto

data class Disease(
    val fullName: String,
    val abbreviation: String,
    val inheritance: List<String>
)

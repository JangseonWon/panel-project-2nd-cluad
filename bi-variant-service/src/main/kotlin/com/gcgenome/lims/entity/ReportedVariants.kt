package com.gcgenome.lims.entity

import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("reported_variants")
data class ReportedVariants(
    @Id val sample: Long,
    val service: String,
    val variants: Json
)
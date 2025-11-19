package com.gcgenome.lims.projection

import org.springframework.data.annotation.Id

data class ReportedVariants(
    @Id val sample: Long,
    val service: String,
    val variants: Any//List<Any>
)
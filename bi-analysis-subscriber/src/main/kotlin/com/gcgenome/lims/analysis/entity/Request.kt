package com.gcgenome.lims.analysis.entity

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table("panel.request000")
data class Request (
    val sample: Long,
    val service: String,
    val service_name: String,
    val organization: String?,
    val organization_name: String?,
    val patient_name: String,
    val age: Int?,
    val sex: String?,
    val birth: LocalDate?,
    val mrn: String?,
    val sample_type: String?,
    val date_sampling: LocalDateTime?,
    val remark: String?
) {
    companion object {
        data class RequestPK (
            val sample: Long,
            val service: String,
        )
    }
}
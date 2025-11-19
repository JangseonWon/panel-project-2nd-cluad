package com.gcgenome.lims.analysis.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("panel.panel_type")
data class PanelType (
    @Id val id: Long,
    val panel: String,
    val type: String?,
    val service: String,
    @Column("effective_date") val effectiveDate: LocalDateTime,
    @Column("expiration_date") val expirationDate: LocalDateTime
)
package com.gcgenome.lims.entity

import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.*
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("panel.analysis")
data class Analysis (
    @Column("sheet") val sheet: UUID,
    @Column("batch") val batch: String,
    @Column("row") val row: Int,
    @Column("request") val request: String,
    @Column("sample") val sample: Long,
    @Column("service") val service: String,
    @Column("serial") val serial: String,
    @Column("panel") val panel: String,
    @Column("value") val value: Json?,
    @Column("create_time") val createAt: LocalDateTime,
    @Column("last_modify_user") val lastModifyBy: String?,
    @Column("last_modify_at") val lastModifyAt: LocalDateTime?
) {
    @Id @Transient val _id: AnalysisPK = AnalysisPK(sheet, batch, row, request)
    companion object {
        data class AnalysisPK (
            val sheet: UUID,
            val batch: String,
            val row: Int,
            val request: String,
        )
    }
}
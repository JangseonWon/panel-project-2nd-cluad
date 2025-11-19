package com.gcgenome.lims.entity

import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.*
import org.springframework.data.domain.Persistable
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
    @Column("service") val service: String
): Persistable<Analysis.Companion.AnalysisPK> {
    @Column("serial") var serial: String = ""
    @Column("panel") var panel: String = ""
    @Column("value") var value: Json = Json.of("{}")

    @CreatedDate @Column("create_time") lateinit var createAt: LocalDateTime
    @LastModifiedBy @Column("last_modify_user") lateinit var lastModifyBy: String
    @LastModifiedDate @Column("last_modify_at") lateinit var lastModifyAt: LocalDateTime
    @Id @Transient val _id: AnalysisPK = AnalysisPK(sheet, batch, row, request)
    override fun getId(): AnalysisPK = _id
    override fun isNew(): Boolean = this::createAt.isInitialized.not()
    companion object {
        data class AnalysisPK (
            val sheet: UUID,
            val batch: String,
            val row: Int,
            val request: String,
        )
    }
}
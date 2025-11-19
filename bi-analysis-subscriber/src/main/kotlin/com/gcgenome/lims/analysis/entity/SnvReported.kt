package com.gcgenome.lims.analysis.entity

import org.springframework.data.annotation.*
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("panel.snv")
data class SnvReported (
    @Column("sample") val sample: Long,
    @Column("service") val service: String,
    @Column("snv") var snv: String
): Persistable<SnvReported.Companion.SnvPK> {
    @CreatedDate @Column("create_at") lateinit var createAt: LocalDateTime
    @CreatedBy @Column("create_user") var createBy: String? = null
    @LastModifiedBy @Column("last_modify_user") var lastModifyBy: String? = null
    @LastModifiedDate @Column("last_modify_at") lateinit var lastModifyAt: LocalDateTime
    @Column("class") var classification: String = ""
    @Id @Transient var _id: SnvPK = SnvPK(sample, service, snv)
    override fun getId(): SnvPK = _id
    override fun isNew(): Boolean = this::createAt.isInitialized.not()
    companion object {
        data class SnvPK (
            val sample: Long,
            val service: String,
            val snv: String
        )
    }
}
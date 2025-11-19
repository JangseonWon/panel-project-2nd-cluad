package com.gcgenome.lims.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.*
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

// R2DBC entity에 Relation은 포함하지 않는다.
// Projection에서 처리한다.
@Schema("panel")
@Table("interpretation")
data class Interpretation(
    @Column("sample") val sample: Long,
    @Column("service") val service: String
): Persistable<Interpretation.Companion.InterpretationPK> {
    @CreatedBy @Column("create_user") lateinit var createBy: String
    @CreatedDate @Column("create_at") lateinit var createAt: LocalDateTime
    @LastModifiedBy @Column("last_modify_user") lateinit var lastModifyBy: String
    @LastModifiedDate @Column("last_modify_at") lateinit var lastModifyAt: LocalDateTime
    @Column("value") var json: Json? = null

    @Id @Transient lateinit var _id: InterpretationPK
    constructor(sample: Long, service: String, createBy: String, createAt: LocalDateTime, lastModifiedBy: String, lastModifyAt: LocalDateTime, json: Json): this(sample, service) {
        this.createBy = createBy
        this.createAt = createAt
        this.lastModifyBy = lastModifiedBy
        this.lastModifyAt = lastModifyAt
        this.json = json
    }
    override fun getId(): InterpretationPK {
        return InterpretationPK(sample, service)
    }
    override fun isNew(): Boolean {
        return this::createAt.isInitialized.not()
    }
    companion object {
        data class InterpretationPK(
            val sample: Long,
            val service: String
        )
    }
}

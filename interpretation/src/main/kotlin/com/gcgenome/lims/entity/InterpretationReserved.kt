package com.gcgenome.lims.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.*
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Schema("panel")
@Table("interpretation_reserved")
data class InterpretationReserved (
    @Column("snv") val snv: String,
    @Column("service") val service: String
): Persistable<InterpretationReserved.Companion.InterpretationReservedPK> {
    @CreatedBy @Column("create_user") lateinit var createBy: String
    @CreatedDate @Column("create_at") lateinit var createAt: LocalDateTime
    @Column("interpretation") var interpretation: String? = null

    @Id @Transient lateinit var _id: InterpretationReservedPK
    constructor(snv: String, service: String, createBy: String, createAt: LocalDateTime, interpretation: String): this(snv, service) {
        this.createBy = createBy
        this.createAt = createAt
        this.interpretation = interpretation
    }
    override fun getId(): InterpretationReservedPK {
        return InterpretationReservedPK(snv, service)
    }
    override fun isNew(): Boolean {
        return this::createAt.isInitialized.not()
    }
    companion object {
        data class InterpretationReservedPK(
            val snv: String,
            val service: String
        )
    }
}

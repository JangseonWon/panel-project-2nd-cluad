package com.gcgenome.lims.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Schema("panel")
@Table("snv_consensual_class")
data class SnvConsensualClass(
    @Column("snv") val snv : String = "",
    @Column("class") val clazz : String = "",
    @Column("create_at") val createAt : LocalDateTime = LocalDateTime.now(),
    @Column("last_modify_at") val lastModifiedAt : LocalDateTime = LocalDateTime.now(),
    @Column("create_user") val createUser : String = "",
    @Column("last_modify_user") val last_modified_user : String = "",
    @Column("comment") val comment : String = "최초 제정"
): Persistable<SnvConsensualClass.Companion.SnvConsensualClassPk> {
    @Id @Transient lateinit var _id: SnvConsensualClassPk
    override fun getId(): SnvConsensualClassPk {
        return SnvConsensualClassPk(snv, createAt)
    }
    override fun isNew(): Boolean {
        return true
    }

    companion object {
        data class SnvConsensualClassPk(
            val snv: String,
            val createAt : LocalDateTime
        )
    }
}
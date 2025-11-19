package com.gcgenome.lims.entity

import com.infobip.spring.data.jdbc.annotation.processor.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Schema("panel")
@Table("not_published_snv")
data class SnvToUpdate(
    @Column("snv") val snv : String = "",
    @Column("sample") val sample : Long = 0,
    @Column("service") val service : String = "",
    @Column("class") val clazz : String = "",
    @Column("create_user") val user : String = "",
    @Column("formatted") val snvFormmated : String = ""
): Persistable<SnvToUpdate.Companion.SnvToUpdatePk> {
    @Id @Transient lateinit var _id: SnvToUpdatePk
    override fun getId(): SnvToUpdatePk {
        return SnvToUpdatePk(snv, sample, service)
    }
    override fun isNew(): Boolean {
        return false
    }

    companion object {
        data class SnvToUpdatePk(
            val snv: String,
            val sample: Long,
            val service: String
        )
    }
}
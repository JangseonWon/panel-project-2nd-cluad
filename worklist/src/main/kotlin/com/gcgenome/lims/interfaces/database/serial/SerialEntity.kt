package com.gcgenome.lims.interfaces.database.serial

import org.springframework.data.annotation.*
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("panel.serial")
class SerialEntity (
    @Id private val id: UUID,
    val worklist: UUID,
    val index: Short,
    var infix: String,
    var idx: Short,
    var serial: String
): Persistable<UUID> {
    @LastModifiedDate @Column("last_modify_at") lateinit var lastModifyAt: LocalDateTime
    @LastModifiedBy @Column("last_modify_by") lateinit var lastModifyBy: String
    override fun getId(): UUID = id
    override fun isNew(): Boolean = this::lastModifyAt.isInitialized.not()
}
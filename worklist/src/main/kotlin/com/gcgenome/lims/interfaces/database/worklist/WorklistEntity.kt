package com.gcgenome.lims.interfaces.database.worklist

import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("panel.worklist")
class WorklistEntity (
    val id: UUID,
    val title: String,
    val createAt: LocalDateTime,
    val createUserId: String,
    val createUserName: String,
    val status: String,
    val remark: String?,
    val domain: String,
    val sampleCount: Int,
    val lastModifyAt: LocalDateTime?
)
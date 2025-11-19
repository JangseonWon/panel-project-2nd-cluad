package com.gcgenome.lims.domain

import java.io.Serializable
import java.util.UUID

@JvmRecord
data class Serial (
    val id: UUID,
    val worklist: UUID,
    val index: Short,
    val serial: String,
    val infix: String,
    val idx: Short
): Serializable
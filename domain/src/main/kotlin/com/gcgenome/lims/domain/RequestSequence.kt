package com.gcgenome.lims.domain

import java.io.Serializable
import java.util.*

@JvmRecord
data class RequestSequence (
    val group: UUID,
    val order: Short,
    val info: String,
    val request: Request
): Serializable
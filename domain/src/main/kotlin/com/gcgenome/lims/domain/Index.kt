package com.gcgenome.lims.domain

import java.io.Serializable
import java.util.*

@JvmRecord
data class Index (
    val id: UUID,
    val worklist: UUID,
    val index: Short,
    val i7IndexName: String? = null,
    val i7IndexSequence: String? = null,
    val i5IndexName: String? = null,
    val i5IndexSequence: String? = null,
): Serializable
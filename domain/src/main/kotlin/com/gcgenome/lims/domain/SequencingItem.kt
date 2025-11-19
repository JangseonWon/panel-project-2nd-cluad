package com.gcgenome.lims.domain

import java.io.Serializable
import java.util.*

@JvmRecord
data class SequencingItem (
    val id: UUID,
    val worklist: UUID,
    val index: Short,
    val name: String? = null,
    val status: SequencingStatus? = null
): Serializable {
    enum class SequencingStatus {
        COMPLETE, FAILED
    }
}
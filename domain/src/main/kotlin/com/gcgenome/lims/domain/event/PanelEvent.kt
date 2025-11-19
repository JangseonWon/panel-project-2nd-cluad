package com.gcgenome.lims.domain.event

import java.io.Serializable
import java.util.*

interface PanelEvent<T: Serializable, N: PanelEvent<T, N>>: Serializable {
    fun id(): UUID
    fun type(): Type
    fun param(): T
    enum class Type {
        UPDATE_SERIAL, UPDATE_REQUEST_SEQUENCE, UPDATE_SEQUENCING_INDEX, UPDATE_BATCH_SEQUENCING, UPDATE_SEQUENCING_ITEM
    }
}
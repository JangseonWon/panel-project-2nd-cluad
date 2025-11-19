package com.gcgenome.lims.domain

import java.time.LocalDateTime
import java.time.ZoneId

@JvmRecord
data class Sample (
    val patient: Patient,
    val id: Long,
    val type: String? = null,
    val age: Int? = null,
    val barcode: String? = null,
    val remark: String? = null,
    val dateCollection: Long?,
) {
    constructor(patient: Patient,
                id: Long,
                type: String?,
                age: Int?,
                barcode: String?,
                remark: String?,
                dateCollection: LocalDateTime?
    ): this(patient, id, type, age, barcode, remark, dateCollection.toEpochMilli())
    companion object {
        fun LocalDateTime?.toEpochMilli(zoneId: ZoneId = ZoneId.systemDefault()): Long? =
            this?.atZone(zoneId)?.toInstant()?.toEpochMilli()
    }
}
package com.gcgenome.lims.domain

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@JvmRecord
data class Request (
    val sample: Sample,
    val service: Service,
    val requester: Organization,
    val dateRequest: Long?,
    val dateReception: Long?,
    val dateDue: Long?,
    val dateDuePublish: Long?,
    val barcode: String? // 의뢰기관 바코드(Optional)
) {
    constructor(
        sample: Sample,
        service: Service,
        requester: Organization,
        dateRequest: LocalDateTime?,
        dateReception: LocalDateTime?,
        dateDue: LocalDateTime?,
        dateDuePublish: LocalDateTime?,
        barcode: String? = null
    ): this (
        sample=sample,
        service=service,
        requester=requester,
        dateRequest=dateRequest.toEpochMilli(),
        dateReception=dateReception.toEpochMilli(),
        dateDue=dateDue.toEpochMilli(),
        dateDuePublish=dateDuePublish.toEpochMilli(),
        barcode=barcode
    )
    companion object {
        val DF: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        fun LocalDateTime?.toEpochMilli(zoneId: ZoneId = ZoneId.systemDefault()): Long? =
        this?.atZone(zoneId)?.toInstant()?.toEpochMilli()
    }
}
package com.gcgenome.lims.domain

import java.time.LocalDate
import java.time.ZoneId

@JvmRecord
data class Patient (
    val organization: Organization,
    val name: String,
    val code: String?,				    // 주민번호
    val mrn: String?,
    val sex: String?,
    val dateBirth: Long?,			    // 생년월일->나이
    val ward: String?,					// 병동
    val department: String?,			// 진료과
    val physician: String?,				// 주치의
    val info: String?,				    // 임상정보/기타
) {
    constructor(
        organization: Organization,
        name: String,
        code: String?,
        mrn: String?,
        sex: String?,
        dateBirth: LocalDate?,
        ward: String?,
        department: String?,
        physician: String?,
        info: String?
    ) : this(
        organization, name, code, mrn, sex,
        dateBirth?.toEpochMilli(),
        ward, department, physician, info
    )
    companion object {
        fun LocalDate?.toEpochMilli(zoneId: ZoneId = ZoneId.systemDefault()): Long? =
            this?.atStartOfDay()?.atZone(zoneId)?.toInstant()?.toEpochMilli()
    }
}
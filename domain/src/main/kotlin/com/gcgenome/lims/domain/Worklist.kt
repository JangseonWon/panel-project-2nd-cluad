package com.gcgenome.lims.domain

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

@JvmRecord
data class Worklist (
    val id: UUID,
    val createAt: String,
    val createBy: String,
    val lastModifyAt: String?,
    val title: String,
    val status: String,
    val remark: String?,
    val domain: String,
    val sampleCount: Int,
) {
    constructor(id: UUID,
                createAt: LocalDateTime,
                createBy: String,
                lastModifyAt: LocalDateTime?,
                title: String,
                status: String,
                remark: String?,
                domain: String,
                sampleCount: Int
    ) : this(id=id,
        createAt=createAt.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Seoul")).format(DTF),
        createBy = createBy,
        lastModifyAt=lastModifyAt?.atZone(ZoneId.of("UTC"))?.withZoneSameInstant(ZoneId.of("Asia/Seoul"))?.format(DTF),
        title=title,
        status=status,
        remark=remark,
        domain=domain,
        sampleCount = sampleCount)
    companion object {
        val DTF: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}
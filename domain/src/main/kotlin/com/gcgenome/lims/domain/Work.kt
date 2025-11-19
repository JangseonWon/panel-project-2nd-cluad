package com.gcgenome.lims.domain

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@JvmRecord
data class Work (
    val worklist: UUID,
    val worklistTitle: String,
    val index: Short,
    val type: String,
    val gid: String,
    val createAt: String,
    val createUser: String,
    val requests: List<Request>,
    val serial: String?,
    val infix: String?,
    val suffix: Short?,
    val sequencingIndex: Index?,
    val sequencing: SequencingItem?
) {
    constructor(worklist: UUID,
                worklistTitle: String,
                index: Short,
                type: String,
                gid: String,
                serial: String?,
                infix: String?,
                suffix: Short?,
                createAt: LocalDateTime,
                createUser: String,
                requests: List<Request>,
                sequencingIndex: Index?,
                sequencing: SequencingItem?
    ) : this(
        worklist = worklist,
        worklistTitle = worklistTitle,
        index = index,
        type = type,
        gid = gid,
        serial = serial,
        infix = infix,
        suffix = suffix,
        createAt= createAt.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Seoul")).format(DTF),
        createUser = createUser,
        requests = requests,
        sequencingIndex = sequencingIndex,
        sequencing = sequencing
    )
    companion object {
        val DTF: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}
package com.gcgenome.lims.interfaces.database.work

import com.gcgenome.lims.domain.SequencingItem
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Table("panel.work")
class WorkEntity (
    val worklist: UUID,
    val worklistTitle: String,
    val index: Short,
    val type: String,
    val gid: String,
    val infix: String?,
    val idx: Short?,
    val createAt: LocalDateTime,
    val createUser: String,
    val organization: String,
    val organizationName: String?,
    val organizationRegNo: String?,
    val department: String?,
    val physician: String?,
    val ward: String?,
    val patientName: String,
    val patientCode: String?,
    val sex: String?,
    val birth: LocalDate?,
    val age: Int?,
    val mrn: String?,
    val sample: Long,
    val sampleType: String?,
    val service: String,
    val serviceName: String,
    val requester: String,
    val requesterName: String?,
    val dateRequest: LocalDateTime?,
    val dateStart: LocalDateTime?,
    val dateReception: LocalDateTime?,
    val dateDue: LocalDateTime?,
    val dateDuePublish: LocalDateTime?,
    val dateSampling: LocalDateTime?,
    val tat: String?,
    val info: String?,
    val barcode: String?,
    val remark: String?,
    val register: Boolean?,
    val cancel: Boolean?,
    val delete: Boolean?,
    val serialId: UUID?,
    val serial: String?,
    val indexId: UUID?,
    val i7IndexName: String?,
    val i7IndexSequence: String?,
    val i5IndexName: String?,
    val i5IndexSequence: String?,
    val batchId: UUID?,
    val sequencingTitle: String?,
    val sequencingFileName: String?,
    val sequencingStatus: SequencingItem.SequencingStatus?
)
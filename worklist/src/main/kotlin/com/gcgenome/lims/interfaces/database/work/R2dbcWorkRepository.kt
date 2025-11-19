package com.gcgenome.lims.interfaces.database.work

import com.gcgenome.lims.domain.*
import com.gcgenome.lims.usecase.serial.PreviousWorkRepository
import com.gcgenome.lims.usecase.work.WorkRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.util.*

@Repository
class R2dbcWorkRepository (
    private val template: R2dbcEntityTemplate
): WorkRepository, PreviousWorkRepository {
    // 쿼리 결과를 Work로 그룹화하고, 그룹마다 Request를 정리해서 Work 목록을 반환한다.
    // 같은 Work에 대해 여러 Request가 매핑되는 경우가 존재한다.
    @Transactional(readOnly = true)
    override fun findByWorklist(id: UUID): Flux<Work> = template.select(query(where("worklist").`is`(id)), WorkEntity::class.java)
        .collectList().flatMapMany { entities -> entities.groupByWorklistAndIndex()
            .map { (_, group) -> createWorkFromGroup(group) }
            .toFlux()
        }

    private fun List<WorkEntity>.groupByWorklistAndIndex(): Map<Pair<UUID, Short>, List<WorkEntity>> = groupBy { Pair(it.worklist, it.index) }
    private fun createWorkFromGroup(entityGroup: List<WorkEntity>): Work {
        val representative = entityGroup.first()
        val requests = entityGroup.map { request(it) }
        return map(representative, requests)
    }
    private fun map(entity: WorkEntity, requests: List<Request>): Work = Work(
        worklist = entity.worklist,
        worklistTitle = entity.worklistTitle,
        index = entity.index,
        type = entity.type,
        gid = entity.gid,
        serial = entity.serial,
        infix = entity.infix,
        suffix = entity.idx,
        createAt = entity.createAt,
        createUser = entity.createUser,
        requests = requests,
        sequencingIndex = index(entity),
        sequencing = sequencing(entity)
    )

    private fun organization(entity: WorkEntity): Organization = Organization(entity.organization, entity.organizationName?:"-")
    private fun patient(entity: WorkEntity): Patient = Patient(
        organization = organization(entity),
        name = entity.patientName,
        code = entity.patientCode,
        mrn = entity.mrn,
        sex = entity.sex,
        dateBirth = entity.birth,
        ward = entity.ward,
        department = entity.department,
        physician = entity.physician,
        info = entity.info
    )
    private fun sample(entity: WorkEntity): Sample = Sample (
        patient = patient(entity),
        id = entity.sample,
        type = entity.sampleType,
        age = entity.age,
        barcode = entity.barcode,
        remark = entity.remark,
        dateCollection = entity.dateSampling
    )
    private fun service(entity: WorkEntity): Service = Service (
        id = entity.service,
        name = entity.serviceName
    )
    private fun requester(entity: WorkEntity): Organization = Organization(entity.requester, entity.requesterName?:"-")
    private fun request(entity: WorkEntity): Request = Request (
        sample = sample(entity),
        service = service(entity),
        requester = requester(entity),
        dateRequest = entity.dateRequest,
        dateReception = entity.dateReception,
        dateDue = entity.dateDue,
        dateDuePublish = entity.dateDuePublish
    )
    private fun index(entity: WorkEntity): Index? = entity.indexId?.let {
        Index (
            id = it,
            worklist = entity.worklist,
            index = entity.index,
            i7IndexName = entity.i7IndexName,
            i7IndexSequence = entity.i7IndexSequence,
            i5IndexName = entity.i5IndexName,
            i5IndexSequence = entity.i5IndexSequence
        )
    }
    private fun sequencing(entity: WorkEntity): SequencingItem? = entity.batchId?.let {
        SequencingItem(
            id = it,
            worklist = entity.worklist,
            index = entity.index,
            name = entity.sequencingFileName,
            status = entity.sequencingStatus
        )
    }

    override fun findPrevious(works: List<Work>): Flux<Work> = template.select(query(where("serial").isNotNull.and("sample").`in`(
        works.flatMap { work -> work.requests }
            .map { request -> request.sample.id }
            .distinct()
    )), WorkEntity::class.java).collectList().flatMapMany { entities -> entities.groupByWorklistAndIndex()
        .map { (_, group) -> createWorkFromGroup(group) }
        .toFlux()
    }
}
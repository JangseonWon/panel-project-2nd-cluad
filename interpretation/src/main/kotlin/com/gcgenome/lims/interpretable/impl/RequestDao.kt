package com.gcgenome.lims.interpretable.impl

import com.gcgenome.lims.entity.QRequest.request
import com.gcgenome.lims.entity.Request

import com.gcgenome.lims.service.Repository
import com.querydsl.core.types.Projections
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RequestDao(private val repo: Repository) {
    fun findById(sample: Long, service: String): Mono<Request> {
        return repo.query {
            it.select(
                Projections.constructor(
                    Request::class.java,
                    request.sample,
                    request.service,
                    request.organization,
                    request.organizationName,
                    request.sampleType,
                    request.age,
                    request.patientName,
                    request.patientCode,
                    request.mrn,
                    request.sex,
                    request.birth,
                    request.info,
                    request.register,
                    request.cancel,
                    request.delete
                )
            ).from(request).where(request.sample.eq(sample).and(request.service.eq(service))) }.one()
    }
}
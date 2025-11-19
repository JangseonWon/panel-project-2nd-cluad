package com.gcgenome.lims.analysis.repository

import com.gcgenome.lims.analysis.entity.Request
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface RequestRepository : ReactiveCrudRepository<Request, Request.Companion.RequestPK> {
    @Query("SELECT * FROM panel.request000 WHERE " +
            "sample IN (SELECT id FROM sample where patient=(SELECT patient FROM sample WHERE id=:sample)) " +
            "AND date_request >= (SELECT max(date_request)+'-1 week' FROM request WHERE sample=:sample) " +
            "AND date_request <= (SELECT max(date_request)+'1 week' FROM request WHERE sample=:sample)")
    fun findRequestBySample(sample: Long): Flux<Request>
}
package com.gcgenome.lims.service

import com.gcgenome.lims.entity.ReportedVariants
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface Repository: R2dbcRepository<ReportedVariants, String> {
    fun findBySample(sample:Long): Flux<ReportedVariants>
}
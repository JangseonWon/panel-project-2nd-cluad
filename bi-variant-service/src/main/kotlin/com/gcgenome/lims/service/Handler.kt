package com.gcgenome.lims.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.projection.ReportedVariants
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class Handler(val repo: Repository, val om: ObjectMapper) {
    fun findAll(): Flux<ReportedVariants> = repo.findAll().map {
        val variants = om.readValue(it.variants.asString(), List::class.java)
        ReportedVariants(it.sample, it.service, variants)
    }
    fun findOne(sample: Long): Flux<ReportedVariants> = repo.findBySample(sample).map {
        val variants = om.readValue(it.variants.asString(), List::class.java)
        ReportedVariants(it.sample, it.service, variants)
    }
}
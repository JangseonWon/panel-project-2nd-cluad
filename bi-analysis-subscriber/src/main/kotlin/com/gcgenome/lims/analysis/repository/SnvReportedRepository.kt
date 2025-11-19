package com.gcgenome.lims.analysis.repository

import com.gcgenome.lims.analysis.entity.SnvReported
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface SnvReportedRepository : ReactiveCrudRepository<SnvReported, SnvReported.Companion.SnvPK> {
    fun findAllBySampleAndService(sample: Long, service: String): Flux<SnvReported>
    fun findBySampleAndServiceAndSnv(sample: Long, service: String, snv: String): Mono<SnvReported>
    @Modifying
    @Query("UPDATE panel.snv SET class=$4 WHERE sample=$1 AND service=$2 AND snv=$3")
    fun updateSnvReported(sample: Long, service: String, snv: String, clazz: String): Mono<Void>
    @Modifying
    fun deleteAllBySampleAndServiceAndSnv(sample: Long, service: String, snv: String): Mono<Void>
}
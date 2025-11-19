package com.gcgenome.lims.service

import com.gcgenome.lims.entity.SnvReported
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface SnvReportedRepository : ReactiveCrudRepository<SnvReported, SnvReported.Companion.SnvPK> {
    fun findAllBySampleAndService(sample: Long, service: String): Flux<SnvReported>
    @Query("SELECT * FROM panel.snv s WHERE snv LIKE :snv ")
    fun findBySnvContaining(snv: String): Flux<SnvReported>
    fun findBySampleAndServiceAndSnv(sample: Long, service: String, snv: String): Mono<SnvReported>
    @Modifying
    fun deleteAllBySampleAndServiceAndSnv(sample: Long, service: String, snv: String): Mono<Void>
    @Modifying
    @Query("UPDATE panel.snv SET class=$4 WHERE sample=$1 AND service=$2 AND snv=$3")
    fun updateClassBySampleAndServiceAndSnv(sample: Long, service: String, snv: String, clazz: String): Mono<Void>
}
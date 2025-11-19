package com.gcgenome.lims.service

import com.gcgenome.lims.entity.Analysis
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface AnalysisRepository : ReactiveCrudRepository<Analysis, Analysis.Companion.AnalysisPK> {
    @Query("SELECT * FROM panel.analysis WHERE batch=:batch AND row=:row AND request=:request")
    fun findAnalysis(batch: String, row: Int, request: String): Mono<Analysis>
    @Query("SELECT * FROM panel.analysis WHERE sample=:sample")
    fun findBySample(sample: Long): Flux<Analysis>
}
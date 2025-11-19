package com.gcgenome.lims.interpretable.impl

import com.gcgenome.lims.entity.Analysis
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import java.util.*

interface AnalysisRepository : ReactiveCrudRepository<Analysis, Analysis.Companion.AnalysisPK> {
    @Query("SELECT * FROM panel.analysis WHERE sample=:sample AND service=:service")
    fun findAnalysis(sample: Long, service: String): Flux<Analysis>
}
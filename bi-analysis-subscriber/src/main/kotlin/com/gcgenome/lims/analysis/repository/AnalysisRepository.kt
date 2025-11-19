package com.gcgenome.lims.analysis.repository

import com.gcgenome.lims.analysis.entity.Analysis
import io.r2dbc.postgresql.codec.Json
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.*

interface AnalysisRepository : ReactiveCrudRepository<Analysis, Analysis.Companion.AnalysisPK> {
    @Query("SELECT * FROM panel.analysis WHERE sheet=:sheet AND batch=:batch AND row=:row AND request=:request")
    fun findAnalysis(sheet: UUID, batch: String, row: Int, request: String): Mono<Analysis>
    @Modifying
    @Query("UPDATE panel.analysis SET serial=:serial, panel=:panel, value=:value WHERE sheet=:sheet AND batch=:batch AND row=:row AND request=:request")
    fun updateAnalysis(sheet: UUID, batch: String, row: Int, request: String, serial: String, panel: String, value: Json): Mono<Void>
}
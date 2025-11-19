package com.gcgenome.lims.analysis

import com.gcgenome.lims.EventConfig
import com.gcgenome.lims.analysis.entity.Analysis
import com.gcgenome.lims.analysis.entity.PanelType
import com.gcgenome.lims.analysis.repository.AnalysisRepository
import com.gcgenome.lims.analysis.repository.RequestRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class AnalysisManager (
    private val analysisRepo: AnalysisRepository,
    private val requestRepo: RequestRepository,
    private val panelTypeManager: PanelTypeManager,
    private val eventConfig: EventConfig
) {
    fun findEntity(sheet: UUID, batch: String, row: Int, sample: Long, panel: String, publish: Boolean): Flux<Analysis> {
        val targetServices = panelTypeManager.subpanels(panel)?.mapNotNull(PanelType::service).orEmpty().toSet()
        return requestRepo.findRequestBySample(sample)
            .filter{ request -> targetServices.contains(request.service) }
            .collectList()
            .flatMapMany { filteredRequests ->
                if (filteredRequests.isEmpty()) return@flatMapMany Flux.error(IllegalArgumentException("No valid services found for panel [$panel]. Allowed services: $targetServices"))
                Flux.fromIterable(filteredRequests)
            }.flatMap {
                val request = "${it.sample}:${it.service}"
                analysisRepo.findAnalysis(sheet, batch, row, request)
                    .switchIfEmpty(Mono.just(
                        Analysis(sheet=sheet, batch=batch, row=row, request=request, sample=it.sample, service=it.service)
                    )) .doOnNext { analysis -> if(publish) eventConfig.publish(it, analysis) }
            }
    }
    fun merge(entity: Analysis): Mono<Analysis> {
        return if(entity.isNew) analysisRepo.save(entity)
        else analysisRepo.updateAnalysis(entity).then(Mono.just(entity))
    }
    private fun AnalysisRepository.updateAnalysis(analysis: Analysis): Mono<Void> = updateAnalysis(analysis.sheet, analysis.batch, analysis.row, analysis.request, analysis.serial, analysis.panel, analysis.value)
}
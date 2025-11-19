package com.gcgenome.lims.analysis

import com.gcgenome.lims.analysis.entity.PanelType
import com.gcgenome.lims.analysis.entity.SnvReported
import com.gcgenome.lims.analysis.repository.SnvReportedRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class SnvReportedManager (
    private val snvReportedRepo: SnvReportedRepository,
    private val panelTypeManager: PanelTypeManager,
) {
    fun findEntity(sample: Long, service: String, snv: String, classification: String, panel: String): Mono<SnvReported> {
        val targetServices = panelTypeManager.subpanels(panel)?.mapNotNull(PanelType::service).orEmpty().toSet()
        return snvReportedRepo.findBySampleAndServiceAndSnv(sample, service, snv)
            .filter{ targetServices.contains(it.service) }
            .switchIfEmpty(
                Mono.just(SnvReported(sample = sample, service = service, snv = snv).apply {
                    this.classification = classification
                }))
    }
    @Transactional
    fun removeEntityIfBatchMismatch(sample: Long, service: String, batch: String): Mono<Void> {
        return snvReportedRepo.findAllBySampleAndService(sample, service)
            .flatMap { reported ->
                reported.snv.split(":").getOrNull(1)
                    ?.takeIf { it != batch }
                    ?.let { snvReportedRepo.deleteAllBySampleAndServiceAndSnv(reported.sample, reported.service, reported.snv) }
                    ?: Mono.empty()
            }
            .then()
    }
    @Transactional
    fun merge(entity: SnvReported): Mono<SnvReported> {
        return if(entity.isNew) snvReportedRepo.save(entity)
        else snvReportedRepo.updateSnvReported(entity).then(Mono.just(entity))
    }
    private fun SnvReportedRepository.updateSnvReported(snv: SnvReported): Mono<Void> = updateSnvReported(snv.sample, snv.service, snv.snv, snv.classification)
}
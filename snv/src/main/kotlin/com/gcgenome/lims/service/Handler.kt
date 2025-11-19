package com.gcgenome.lims.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.entity.Analysis
import com.gcgenome.lims.entity.SnvReported
import com.gcgenome.lims.search.PageReactive
import com.gcgenome.lims.search.SearchParam
import com.gcgenome.lims.test.BloodCancerPanel
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Service
class Handler(
    private val analysisRepository: AnalysisRepository,
    private val snvRepository: SnvReportedRepository,
    private val snvDao: SnvDao,
    private val om: ObjectMapper
) {
    fun findReportedByRequest(sample: Long, service: String): Flux<Document> {
        return snvRepository.findAllBySampleAndService(sample, service)
            .flatMap { snvReported ->
                val snv = snvReported.snv
                val batch = snv.split(":")[1]
                val row = snv.split(":")[2].toInt()

                snvDao.findById(batch, snv).map { doc ->
                    doc.apply {
                        this["report"] = snvReported.classification
                    }.let { updatedDoc ->
                        Flux.just(updatedDoc).findReported(batch, row, "$sample:$service", service)
                    }
                }
            }
            .flatMap { it }
    }

    fun findReportedByRequestAndBatch(sample: Long, service: String, batch: String): Flux<Document> {
        return snvRepository.findBySnvContaining("%$batch%")
            .flatMap { snvReported ->
                val snv = snvReported.snv
                val batch = snv.split(":")[1]
                val row = snv.split(":")[2].toInt()
                snvDao.findById(batch, snv).map { doc ->
                    doc.apply {
                        this["report"] = snvReported.classification
                    }.let { updatedDoc ->
                        Flux.just(updatedDoc).findReported(batch, row, "$sample:$service", service)
                    }
                }
            }
            .flatMap { it }
    }

    fun findByRequest(sample: Long, service: String, batch: String, row: Int, param: SearchParam): Mono<PageReactive<Document>> {
        val findReported = snvRepository.findAllBySampleAndService(sample, service).collectList().map { it.associateBy(SnvReported::snv) }
        val request = "$sample:$service"
        val findAnalysis = analysisRepository.findAnalysis(batch, row, request)
        return findAnalysis.zipWith(findReported)
            .flatMap { tuple ->
                val analysis = tuple.t1
                val reported = tuple.t2
                snvDao.findByAnalysis(analysis, param).map { page ->
                    PageReactive(page.totalElements, page.pageSize, page.currentPage, page.data.map { doc -> doc.apply {
                        if(reported.containsKey(doc.id)) doc["report"] = reported[doc.id]!!.classification
                    }}.findReported(batch, row, request, service))
                }
            }
    }

    fun Flux<Document>.findReported(batch: String, row: Int, request: String, service: String): Flux<Document> {
        return analysisRepository.findAnalysis(batch, row, request).flatMapMany { analysis ->
            this.collectList().flatMapMany { docs ->
                Flux.fromIterable(docs.map { it["snv"] }).flatMap { snvRepository.findBySnvContaining("%$it") }.collectList()
                    .flatMapMany { list ->
                        Flux.fromIterable(list)
                            .flatMap { snv -> analysisRepository.findBySample(snv.id.sample).map { analysis -> Pair(snv, analysis) } }
                            .collectMap({ it.first }, { it.second })
                            .flatMapMany { snvAnalysisMap ->
                                val reported = list.groupBy { snvReported -> extractSnvFromSnvId(snvReported.snv) }
                                    .mapValues { (_, snvReportedList) ->
                                        snvReportedList.map { snvReported ->
                                            val classification = snvReported.classification
                                            if (isBloodCancerPanel(snvReported.service)) "${snvReported.sample}:${snvReported.service}:${snvAnalysisMap[snvReported]?.panel}=$classification"
                                            else "${snvReported.sample}:${snvReported.service}=$classification"
                                        }
                                    }
                                updateDocumentsWithReportedSnv(docs, reported, service, analysis)
                            }
                    }
            }
        }
    }

    private fun updateDocumentsWithReportedSnv(docs: List<Document>, reported: Map<String?, List<String>>, service: String, analysis: Analysis) =
        Flux.fromIterable(docs).map { doc ->
            doc.apply {
                val reportedSnv = reported.keys.find { this.id.toString().contains(it.toString()) }
                if (reportedSnv != null) {
                    this["reported"] = if (!isBloodCancerPanel(service)) reported[reportedSnv]
                    else reported[reportedSnv]?.filter {
                        val parts = it.split(":")
                        (parts.size > 2) && (parts[2].split("=")[0] == analysis.panel) }
                }
            }
        }

    private fun isBloodCancerPanel(service: String) = BloodCancerPanel.values().any { service.equals(it.code, ignoreCase = true) }

    private fun extractSnvFromSnvId(input: String): String? {
        val parts = input.split(":")
        return if (parts.size >= 5) {
            parts.slice(3..parts.size - 1).joinToString(":")
        } else null
    }

    fun findAnalysis(sample: Long): Mono<String> = analysisRepository.findBySample(sample).map { om.writeValueAsString(it) }
        .collectList().map { list-> "[${list.stream().collect(Collectors.joining(","))}]" }

    @Transactional
    fun create(sample: Long, service: String, variant: String, classification: String): Mono<Void> = snvRepository
        .findBySampleAndServiceAndSnv(sample, service, variant)
        .flatMap { snvRepository.updateClassBySampleAndServiceAndSnv(sample, service, variant, classification).then(Mono.just(it)) }
        .switchIfEmpty(
            Mono.just(SnvReported(sample=sample, service=service, snv=variant).apply {
                this.classification = classification
            }).flatMap(snvRepository::save)
        ).then()

    @Transactional
    fun delete(sample: Long, service: String, variant: String): Mono<Void> = snvRepository.deleteAllBySampleAndServiceAndSnv(sample, service, variant)
}
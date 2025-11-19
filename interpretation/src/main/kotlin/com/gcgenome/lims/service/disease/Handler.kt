package com.gcgenome.lims.service.disease

import com.gcgenome.lims.dto.Disease
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux

@Service("DiseaseHandler")
class Handler(val dao: Dao) {
    @Transactional(readOnly = true)
    fun disease(gene: String): Flux<Disease> = dao.findByGene(gene)
}
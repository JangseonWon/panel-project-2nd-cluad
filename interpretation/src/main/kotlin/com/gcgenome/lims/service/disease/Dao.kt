package com.gcgenome.lims.service.disease

import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.entity.QDiseasePredefined.diseasePredefined
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component("DiseaseDao")
class Dao(private val repo: Repository) {
    fun findByGene(gene: String): Flux<Disease> {
        return repo.query {
            it.select(diseasePredefined).from(diseasePredefined).where(diseasePredefined.gene.eq(gene))
        }.all().map { Disease(it.disease, it.abbreviation, it.inheritance.split(",").stream().map(String::trim).toList()) }
    }
}
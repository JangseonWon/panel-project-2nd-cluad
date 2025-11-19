package com.gcgenome.lims.usecase.worklist

import com.gcgenome.lims.domain.Search
import com.gcgenome.lims.domain.Worklist
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class WorklistSearchService(private val repo: WorklistRepository) {
    fun search(param: Search): Mono<Page<Worklist>> = param.copy (
        filters = param.filters + ("domain" to DOMAIN) + ("without_disposal" to "true")
    ).let(repo::search)
    companion object {
        const val DOMAIN = "HRD,IDT,Onco,TSO_CT,TSO_ST"
    }
}
package com.gcgenome.lims.interpretable.impl

import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
class SnvDao(val em: ReactiveElasticsearchTemplate) {
    fun find(batch: String, id: String): Mono<Document> {
        return em.get(id, Document::class.java, IndexCoordinates.of(("analysis-snv-$batch").lowercase(Locale.getDefault())))
    }
}
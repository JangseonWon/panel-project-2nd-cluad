package com.gcgenome.lims.analysis

import com.gcgenome.lims.analysis.entity.Analysis
import org.slf4j.LoggerFactory
import org.springframework.data.elasticsearch.core.document.Document
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

abstract class AbstractSnvFileReader (
    val analysisManager: AnalysisManager,
    val snvManager: SnvManager,
    val mutex: Mutex,
    private val sheet: UUID
): FileReader, AbstractSnvNormalizer {
    private val logger = LoggerFactory.getLogger(javaClass)
    abstract fun format(path: Path): SnvFileFormat?
    override fun exec(file: Path) {
        logger.info("exec: Annotation Filter")
        val format = format(file) ?: return
        if(format.sample==null) return
        val snvs = read(file).map { snv -> snv.normalize().appendClassOrder() }.associateBy { IdGenerator.snvId(it) }
        logger.info("size:${snvs.size}")
        val lock = "${format.batch}:${format.sample}:${format.subpanel}"
        mutex.acquire(lock).thenMany(analysisManager.findEntity(sheet, format.batch, format.row, format.sample, format.panel, false))
            .map {
                it.apply {
                    serial = format.serial
                    panel = format.subpanel
                    this.file = file.name
                } }.flatMap(analysisManager::merge)
            .flatMap { analysis ->
                val map = snvs.mapValues { (_, map) -> map.findDocumentOrCreate(analysis).map { it.appendTag(format.tag) } }
                val docs = Flux.fromIterable(map.values).flatMap { it }
                snvManager.merge(analysis, docs)
            }.then().doFinally { mutex.release(lock) }.block()
    }
    fun MutableMap<String, String>.findDocumentOrCreate(analysis: Analysis): Mono<Document> {
        val snvId = IdGenerator.snvId(this)
        val id = IdGenerator.analysisSnvId(analysis, snvId)
        return snvManager.find(analysis, id)
            .map { it.also {
                for(key in this.keys) if(it.containsKey(key).not()) it[key.replace(" ", "_")] = this[key]!!
            } }.switchIfEmpty(Mono.just(Document.from(this).apply {
                this["id"] = id
                this["analysis"] = IdGenerator.analysisId(analysis)
                this["snv"] = snvId
            }))
    }
    fun Document.appendTag(tag: String): Document = this.apply {
        if(this.containsKey("tags").not()) this["tags"] = listOf(tag)
        else this["tags"] = (this["tags"] as List<String>).plus(tag).distinct()
    }
    data class SnvFileFormat (
        val serial: String,
        val sample: Long?,
        val panel: String,
        val subpanel: String,
        val batch: String,
        val row: Int,
        val tag: String
    ) {
        override fun toString(): String = "SnvFileFormat[sample=$sample, panel=$panel, subpanel=$subpanel, batch=$batch, row=$row, serial=$serial, tag=$tag]"
    }
}
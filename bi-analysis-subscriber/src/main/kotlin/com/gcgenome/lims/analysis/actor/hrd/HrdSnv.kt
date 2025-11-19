package com.gcgenome.lims.analysis.actor.hrd

import com.gcgenome.lims.analysis.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Path
import java.util.*
import kotlin.io.path.name

@Service
class HrdSnv(
    analysisManager: AnalysisManager,
    snvManager: SnvManager,
    private val snvReportedManager: SnvReportedManager,
    @Value("\${subscriber.hrd.sheet}") val sheet: UUID,
    mutex: Mutex
): Hrd, AbstractSnvFileReader(analysisManager, snvManager, mutex, sheet) {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun chkFormat(file: Path): Boolean = HrdFileFormatter.match(file.name)!=null
    override fun format(path: Path): SnvFileFormat? = HrdFileFormatter.match(path.name)
    override fun batch(path: Path): String? = HrdFileFormatter.match(path.name)?.batch
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
                val map: Map<String, Mono<Document>> = snvs.mapValues { (_, map) -> map.findDocumentOrCreate(analysis).map { it.appendTag(format.tag) } }
                val docs: Flux<Document> = Flux.fromIterable(map.values).flatMap { it }
                val snvs: Flux<Document> = snvManager.merge(analysis, docs)
                snvReportedManager.removeEntityIfBatchMismatch(analysis.sample, analysis.service, analysis.batch).subscribe()
                snvs.filter { document ->
                    val classification = document.getString("tier")
                    classification != null && !classification.equals("#") && !classification.equals("#LowVAF")}
                    .flatMap { document ->
                        val snv = document.getString("id")
                        val classification = document.getString("tier")
                        Flux.just(snv to classification)
                    }.flatMap { (snv, classification) ->
                        snvReportedManager.findEntity(analysis.sample, analysis.service, snv, classification, analysis.panel)
                            .flatMap (snvReportedManager::merge)
                    }
            }.then().doFinally { mutex.release(lock) }.block()
    }
    object HrdFileFormatter {
        private val matcher = "(^\\d{2}OncoHRD\\d{3})[-_](\\d{2}HRD\\d{3,4})[-_](\\d{8}-\\d{3}-\\d{4})[-_]([a-zA-Z]\\d{1,2})[-_]".toRegex()

        fun match(fileName: String): SnvFileFormat? {
            val match = matcher.find(fileName) ?: return null
            return SnvFileFormat(
                serial = match.groupValues[0].removeSuffix("_").removeSuffix("-"),
                batch = match.groupValues[1],
                panel = "HRD",
                subpanel = "HRD",
                sample = match.groupValues[3].replace("-", "").toLong(),
                row = match.groupValues[4].replace(Regex("[^0-9]"), "").toInt(),
                tag = tag(fileName)
            )
        }
        private fun tag(fileName: String): String = when {
            fileName.lowercase().endsWith("annotation_filter_candidates.txt") -> "candidate"
            fileName.lowercase().endsWith("annotation_filter.txt") -> "all"
            else -> ""
        }
    }
}
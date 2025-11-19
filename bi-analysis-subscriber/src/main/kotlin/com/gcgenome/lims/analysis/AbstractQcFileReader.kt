package com.gcgenome.lims.analysis

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

abstract class AbstractQcFileReader (
    private val analysisManager: AnalysisManager,
    private val mutex: Mutex,
    private val sheet: UUID,
    private val om: ObjectMapper,
    private val delimiter: String = ","
): FileReader, AbstractSnvNormalizer {
    private val logger = LoggerFactory.getLogger(javaClass)
    abstract fun header(header: String): QcRowHeader?
    override fun exec(file: Path) {
        logger.info("exec: Annotation Filter")
        val colHeader = Files.lines(file, Charsets.UTF_8).limit(1).flatMap { line -> line.split(delimiter).stream() }.map { word -> word.trim() }.toList()
        Files.readAllLines(file, Charsets.UTF_8)
            .drop(1)
            .filter { line -> line.trim().isNotBlank() }
            .map { line ->
                val row = line.split(delimiter).map { word -> word.trim() }
                val rowHeader = header(row[0])
                if(rowHeader?.sample == null) null
                else update(rowHeader, row.toMap(colHeader)).block()
            }
    }
    private fun List<String>.toMap(header: List<String>): Map<String, String> = this.withIndex().associate { (index, value) -> header[index] to value }
    private fun update(rowHeader: QcRowHeader, value: Map<String, String>): Mono<Void> {
        val lock = "${rowHeader.batch}:${rowHeader.sample}:${rowHeader.subpanel}"
        return mutex.acquire(lock).thenMany(analysisManager.findEntity(sheet, rowHeader.batch, rowHeader.row, rowHeader.sample!!, rowHeader.panel, true))
            .map {
                it.apply {
                    serial = rowHeader.serial
                    panel = rowHeader.subpanel
                    this.value = Json.of(om.writeValueAsBytes(value))
                }
            }.flatMap(analysisManager::merge)
            .then().doFinally { mutex.release(lock) }
    }
    data class QcRowHeader (
        val serial: String,
        val sample: Long?,
        val panel: String,
        val subpanel: String,
        val batch: String,
        val row: Int
    ) {
        override fun toString(): String = "QcRowHeader[sample=$sample, panel=$panel, subpanel=$subpanel, batch=$batch, row=$row, serial=$serial]"
    }
}
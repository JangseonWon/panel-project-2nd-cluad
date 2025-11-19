package com.gcgenome.lims.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.analysis.AnalysisManager
import com.gcgenome.lims.analysis.Mutex
import com.gcgenome.lims.analysis.SnvManager
import com.gcgenome.lims.analysis.actor.hema.*
import com.gcgenome.lims.analysis.actor.hema.v1.HemaSnv
import com.gcgenome.lims.analysis.actor.hema.v1.WardenHema
import com.gcgenome.lims.analysis.entity.Analysis
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.*
import kotlin.io.path.name

@SpringBootTest(classes = [
    WardenHema::class, Mutex::class, HemaSnv::class, HemaQc::class, ObjectMapper::class,
    WardenHemaTest.Companion.Config::class])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class WardenHemaTest {
    @Test
    @Order(2)
    @DisplayName("Hema File Formatter test")
    internal fun fileFormatTest() {
        Files.newDirectoryStream(path).forEach { child ->
            val parsed =  HemaSnv.HemaFileFormatter.match( child.name)
            if(child.name.lowercase().endsWith("qc.csv")) assertNull(parsed)
            else assertNotNull(parsed)
        }
    }
    @Test
    @DisplayName("Hema ward Test")
    internal fun test(@Value("\${subscriber.hema.sheet}") sheet: UUID) {
        val targetOrigin = path.resolve("I_23HEMA037_AML-04_20230510-171-5260_S62.annotation_filter.txt")
        val target = Files.move(targetOrigin, path.parent.resolve(targetOrigin.name))
        Thread.sleep(1000)

        Files.move(target, targetOrigin)
        Thread.sleep(4000)
        val success = analysisManager.findEntity(sheet, "23HEMA037", 4, 202305101715260, "AML", false)
            .filter { (it.panel == "AML") && (it.serial == "I_23HEMA037_AML-04_20230510-171-5260") }
            .count().block()
        assertEquals(2, success)
    }

    companion object {
        private lateinit var analysisManager: AnalysisManager
        private lateinit var snvManager: SnvManager
        private lateinit var path: Path
        @BeforeAll
        @JvmStatic
        fun prepare(@Autowired instance: WardenHema, @Value("\${subscriber.hema.path}") _path: String) {
            path = Path.of(_path)
            runBlocking {
                launch{  instance.ward() }
            }
        }
        @TestConfiguration
        class Config {
            @Bean
            fun mockAnalysisManager(): AnalysisManager {
                analysisManager = mockk<AnalysisManager>()
                val data = mutableMapOf<Analysis.Companion.AnalysisPK, Analysis>()
                every { analysisManager.findEntity(any(), any(), any(), any(), any(), any()) }.answers {
                    val invocation = it.invocation
                    val sheet = invocation.args[0] as UUID
                    val batch = invocation.args[1] as String
                    val row = invocation.args[2] as Int
                    val sample = invocation.args[3] as Long
                    val service1 = "N064"
                    val request1 = "$sample:$service1"
                    val service2 = "N159"
                    val request2 = "$sample:$service2"
                    val values = listOf(
                        Analysis(sheet, batch, row, request1, sample, service1),
                        Analysis(sheet, batch, row, request2, sample, service2))
                    if(data.containsKey(values[0].id).not()) data[values[0].id] = values[0]
                    if(data.containsKey(values[1].id).not()) data[values[1].id] = values[1]
                    Flux.fromIterable(data.values)
                }
                every { analysisManager.merge(any()) }.answers {
                    val invocation = it.invocation
                    val entity = invocation.args[0] as Analysis
                    entity.createAt = LocalDateTime.now()
                    entity.lastModifyAt = LocalDateTime.now()
                    data[entity.id] = entity
                    Mono.just(entity)
                }
                return analysisManager
            }
            @Bean
            fun mockSnvManager(@Autowired op: ReactiveElasticsearchTemplate): SnvManager {
                snvManager = mockk<SnvManager>()
                every { snvManager.find(any(), any()) } answers {
                    val analysis = it.invocation.args[0] as Analysis
                    val batch = analysis.batch
                    val index = IndexCoordinates.of(SnvManager.toIndex(batch))
                    val id = it.invocation.args[1] as String
                    op.get(id, Document::class.java, index)
                }
                return snvManager
            }
        }
    }
}
package com.gcgenome.lims.test

import com.gcgenome.lims.analysis.*
import com.gcgenome.lims.analysis.SnvManager.Companion.toCleanDocument
import com.gcgenome.lims.analysis.actor.hrd.HrdSnv
import com.gcgenome.lims.analysis.actor.hrd.WardenHrd
import com.gcgenome.lims.analysis.entity.Analysis
import com.gcgenome.lims.analysis.entity.SnvReported
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.testcontainers.elasticsearch.ElasticsearchContainer
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.*
import kotlin.io.path.name
import kotlin.test.*

@DataElasticsearchTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Import(WardenHrdTest.Companion.ElasticsearchClientConfig::class, WardenHrd::class, Mutex::class, HrdSnv::class, WardenHrdTest.Companion.Config::class)
internal class WardenHrdTest {
    @Test
    @Order(2)
    @DisplayName("Hrd File Formatter test")
    internal fun fileFormatTest() {
        Files.newDirectoryStream(path).forEach { child ->
            val parsed =  HrdSnv.HrdFileFormatter.match(child.name)
            assertNotNull(parsed)
        }
    }
    @Test
    @DisplayName("Hrd ward Test")
    internal fun test(@Value("\${subscriber.hrd.sheet}") sheet: UUID) {
        val sample = 202407199710002
        val service = "N140"
        val batch = "24OncoHRD042"
        val panel = "HRD"
        val serial = "24OncoHRD042_24HRD529_20240719-971-0002_S99"
        val snv = "b83c54fc-9dd9-11eb-bb6b-e4434b780b98:24OncoHRD042:099:hg19:05:131931451:TA:T"
        val classification = "Tier2"
        val targetOrigin = path.resolve("24OncoHRD042_24HRD529_20240719-971-0002_S99_annotation_filter_candidates.txt")
        val target = Files.move(targetOrigin, path.parent.resolve(targetOrigin.name))
        Thread.sleep(1000)

        Files.move(target, targetOrigin)
        Thread.sleep(8000)

        val analysisCount  = analysisManager.findEntity(sheet, batch, 99, sample, panel, false)
            .filter { (it.panel == panel) && (it.serial == serial) }
            .count().block()
        assertEquals(1, analysisCount)

        val snvReported = snvReportedManager.findOrRemoveEntityByBatch(sample, service, snv, classification, panel, batch).block()
        assertEquals(sample, snvReported.sample)
    }

    companion object {
        private val password = "s3cret"
        private lateinit var analysisManager: AnalysisManager
        private lateinit var snvManager: SnvManager
        private lateinit var snvReportedManager: SnvReportedManager
        private lateinit var path: Path

        @BeforeAll
        @JvmStatic
        fun prepare(@Autowired instance: WardenHrd, @Value("\${subscriber.hrd.path}") _path: String) {
            path = Path.of(_path)
            runBlocking {
                launch { instance.ward() }
            }
        }

        @JvmStatic
        private val container =
            ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.7.1").withPassword(password)
                .apply { start() }

        @TestConfiguration
        class ElasticsearchClientConfig : ReactiveElasticsearchConfiguration() {
            override fun clientConfiguration(): ClientConfiguration = ClientConfiguration.builder()
                .connectedTo(container.httpHostAddress)
                .usingSsl(container.createSslContextFromCa())
                .withClientConfigurer(
                    ElasticsearchClients.ElasticsearchHttpClientConfigurationCallback { httpAsyncClientBuilder ->
                        httpAsyncClientBuilder.setDefaultCredentialsProvider(BasicCredentialsProvider().apply {
                            setCredentials(AuthScope.ANY, UsernamePasswordCredentials("elastic", password))
                        })
                    }
                ).build()
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
                    val service = "N140"
                    val request = "$sample:$service"
                    val analysis = Analysis(sheet, batch, row, request, sample, service)
                    if (data.containsKey(analysis.id).not()) data[analysis.id] = analysis
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
            fun mockSnvManager(@Autowired mutex: Mutex, @Autowired op: ReactiveElasticsearchTemplate): SnvManager {
                snvManager = mockk<SnvManager>()
                every { snvManager.find(any(), any()) } answers {
                    val analysis = it.invocation.args[0] as Analysis
                    val batch = analysis.batch
                    val index = IndexCoordinates.of(SnvManager.toIndex(batch))
                    val id = it.invocation.args[1] as String
                    op.get(id, Document::class.java, index)
                }

                every { snvManager.merge(any(), any()) } answers {
                    val analysis = it.invocation.args[0] as Analysis
                    val batch = analysis.batch
                    val index = IndexCoordinates.of(SnvManager.toIndex(batch))
                    val createIndexIfNotPresent = mutex.acquire(batch).then(op.indexOps(index).exists()).flatMap {
                        if (it.not()) op.indexOps(index).create(
                            SnvManager.ANALYSIS_SETTING, Document.from(
                                SnvManager.ANALYSIS_MAPPING
                            )
                        )
                        else Mono.just(true)
                    }.doFinally { mutex.release(batch) }.onErrorResume { _ -> Mono.just(true) }
                    val annotations = it.invocation.args[1] as Flux<Document>
                    val cleanThenBulkSave = annotations.map { it.toCleanDocument() }.window(1000).flatMap {
                        it.collectList()
                            .flatMapMany { bulk -> op.saveAll(bulk, index).thenMany(Flux.fromIterable(bulk)) }
                    }
                    createIndexIfNotPresent.thenMany(cleanThenBulkSave)
                }

                return snvManager
            }

            @Bean
            fun mockSnvReportedManager(): SnvReportedManager {
                snvReportedManager = mockk<SnvReportedManager>()
                val data = mutableMapOf<SnvReported.Companion.SnvPK, SnvReported>()
                every { snvReportedManager.findOrRemoveEntityByBatch(any(), any(), any(), any(), any(), any())}.answers {
                    val invocation = it.invocation
                    val sample = invocation.args[0] as Long
                    val service = invocation.args[1] as String
                    val snv = invocation.args[2] as String
                    val snvReported = SnvReported(sample, service, snv)
                    Mono.just(snvReported)
                }
                every { snvReportedManager.merge(any()) }.answers {
                    val invocation = it.invocation
                    val entity = invocation.args[0] as SnvReported
                    entity.createAt = LocalDateTime.now()
                    entity.lastModifyAt = LocalDateTime.now()
                    data[entity.id] = entity
                    Mono.just(entity)
                }
                return snvReportedManager
            }
        }
    }
}
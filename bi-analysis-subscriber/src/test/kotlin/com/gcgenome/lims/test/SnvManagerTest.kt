package com.gcgenome.lims.test

import com.gcgenome.lims.analysis.AbstractSnvNormalizer
import com.gcgenome.lims.analysis.FileReader
import com.gcgenome.lims.analysis.SnvManager
import com.gcgenome.lims.analysis.entity.Analysis
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.testcontainers.elasticsearch.ElasticsearchContainer
import java.nio.file.Path
import java.util.*
import kotlin.test.assertTrue

@DataElasticsearchTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Import(SnvManagerTest.Companion.ElasticsearchClientConfig::class, SnvManager::class)
internal class SnvManagerTest {
    companion object {
        private val password = "s3cret"
        @JvmStatic
        private val container = ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.7.1").withPassword(password).apply { start() }
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
        private lateinit var path: Path
        @BeforeAll
        @JvmStatic
        fun prepare(@Value("\${subscriber.hema.path}") _path: String) {
            path = Path.of(_path)
        }
    }
    @Test
    @DisplayName("SNV Manager 입출력 테스트")
    internal fun test(@Autowired mgr: SnvManager, @Autowired op: ReactiveElasticsearchOperations) {
        val analysis = Analysis(UUID.randomUUID(), "BATCH", 1, "REQUEST", 0, "SERVICE")
        val target = path.resolve("I_23HEMA037_AML-04_20230510-171-5260_S62.annotation_filter.txt")
        val normalizer = object: AbstractSnvNormalizer, FileReader {
            val input = read(target).mapValues { (_, snv) ->
                snv.normalize().appendClassOrder().appendTag("")
            }
            override fun chkFormat(file: Path): Boolean { TODO("Not yet implemented") }
            override fun exec(file: Path) { TODO("Not yet implemented") }
        }
        mgr.merge(analysis, normalizer.input).then().block()
        val index = IndexCoordinates.of("analysis-snv-${analysis.batch.lowercase()}")
        assertTrue(op.indexOps(index).exists().block())
    }
}
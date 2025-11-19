package com.gcgenome.lims.test

import com.gcgenome.lims.analysis.AnalysisManager
import com.gcgenome.lims.analysis.PanelTypeManager
import com.gcgenome.lims.analysis.entity.Analysis
import com.gcgenome.lims.analysis.repository.AnalysisRepository
import com.gcgenome.lims.analysis.repository.PanelTypeRepository
import com.gcgenome.lims.analysis.repository.RequestRepository
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

@DataR2dbcTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@EnableR2dbcRepositories
@Import(PanelTypeManager::class, AnalysisManager::class)
class AnalysisTest {
    companion object {
        private val database = "lims"
        private val user = "panel"
        private val password = "s3cret"

        private val container = PostgreSQLContainer("postgres:15.3-alpine3.18")
            .withDatabaseName(database)
            .withUsername(user)
            .withPassword(password)
            .withInitScript("analysis.sql")
        @JvmStatic
        private val wrapper = PostgreSQLR2DBCDatabaseContainer(container).apply { start() }
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url", Companion::r2dbcUrl)
            registry.add("spring.r2dbc.username", container::getUsername)
            registry.add("spring.r2dbc.password", container::getPassword)
        }
        private fun r2dbcUrl(): String = "r2dbc:postgresql://${container.host}:${container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)}/${container.databaseName}"
    }
    @Test
    @Order(1)
    @DisplayName("R2DBC connection test")
    internal fun r2dbcConnectionTest(@Autowired analysisRepository: AnalysisRepository) {
        assertEquals(analysisRepository.findAll().count().block(), 5)
    }
    @Test
    @Order(2)
    @DisplayName("저장된 PanelType 범위 검색 테스트")
    internal fun panelTypeRangeSelectionTest(@Autowired panelTypeRepository: PanelTypeRepository) {
        assertEquals(panelTypeRepository.getPanelTypeByEffectiveDateLessThanEqualAndExpirationDateGreaterThan(
            LocalDate.of(2023, 1, 1).atStartOfDay(),
            LocalDate.of(2023, 1, 1).atStartOfDay())
            .count().block(), 160)
        assertEquals(panelTypeRepository.getPanelTypeByEffectiveDateLessThanEqualAndExpirationDateGreaterThan(
            LocalDate.of(2023, 2, 1).atStartOfDay(),
            LocalDate.of(2023, 2, 1).atStartOfDay())
            .count().block(), 168)
    }
    @Test
    @Order(3)
    @DisplayName("PanelType manager 동기화 테스트")
    internal fun f(@Autowired panelTypeManager: PanelTypeManager) {
        assertNull(panelTypeManager.subpanels("GMD"))
        panelTypeManager.update()
        Thread.sleep(2000)
        assertEquals(panelTypeManager.subpanels("GMD")?.size, 28)
    }
    @Test
    @Order(2)
    @DisplayName("의뢰번호(Sample ID)로 동일 Request 목록 가져오기")
    internal fun requestTest(@Autowired requestRepository: RequestRepository) {
        assertEquals(requestRepository.findRequestBySample(202303211715096).count().block(), 4)
        assertEquals(requestRepository.findRequestBySample(202212281715194).count().block(), 2)
    }
    @Test
    @Order(4)
    @DisplayName("Request 목록과 패널 정보로 타겟 검사 필터링 테스트")
    internal fun requestPanelTest(@Autowired panelTypeManager: PanelTypeManager, @Autowired analysisManager: AnalysisManager) {
        panelTypeManager.update()
        Thread.sleep(2000) // 패널 정보 동기화 대기

        // 기존에 존재하는 Analysis 리턴
        assertEquals(analysisManager.findEntity(
            UUID.fromString("a118c948-74b9-4929-abf9-35d8a50e6f37"),
            "23BRCA001", 4, 202212281715194, "BRCA", false)
            .filter{ it.isNew.not() }
            .count().block(), 2)

        // 없던 Analysis 새로 생성해서 리턴
        assertEquals(analysisManager.findEntity(
            UUID.fromString("01db4ff4-9689-47b0-b0d9-1659788912c5"),
            "23GMD011", 4, 202303211715096, "GMD", false)
            .filter(Analysis::isNew)
            .count().block(), 4)
    }
}
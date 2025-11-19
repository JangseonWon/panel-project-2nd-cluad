package com.gcgenome.lims.usecase.serial.generator

import com.gcgenome.lims.domain.Request
import com.gcgenome.lims.domain.Work
import com.gcgenome.lims.test.SingleGenePanel
import com.gcgenome.lims.test.SolidTumorPanel
import com.gcgenome.lims.usecase.serial.SerialRepository
import com.gcgenome.lims.usecase.work.WorkRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import reactor.test.StepVerifier
import java.util.*

class WorklistYearyAndGroupableSerialGeneratorTest: ShouldSpec({
    val workRepo: WorkRepository = mockk()
    val repo: SerialRepository = mockk()

    val worklistId = UUID.randomUUID()
    val work1 = mockk<Work>().apply {
        every { worklist } returns worklistId
        every { index } returns 1
        every { requests } returns listOf(mockk<Request>().apply {
            every { service.id } returns SingleGenePanel.Z137.code
        }, mockk<Request>().apply {
            every { service.id } returns SingleGenePanel.Z138.code
        })
    }
    val work2 = mockk<Work>().apply {
        every { worklist } returns worklistId
        every { index } returns 2
        every { requests } returns listOf(mockk<Request>().apply {
            every { service.id } returns SolidTumorPanel.values()[1].code
        })
    }
    val generator = WorklistYearyAndGroupableSerialGenerator(repo)
    should("기존에 등록된 검사코드가 있는 경우 해당 기간 내에 등록된 최신 연번을 기준으로 연번을 생성한다") {
        // GIVEN
        val worklist = listOf(work1, work2)
        val latestIndexes = mutableMapOf (
            SingleGenePanel.Z137.serialGroup to 100.toShort(),
            SolidTumorPanel.values()[1].serialGroup to 20.toShort()
        )
        every { workRepo.findByWorklist(worklistId) } returns Flux.fromIterable(worklist)
        every { repo.findLatest(any()) } returns latestIndexes.entries.map { it.key to it.value }.toFlux()

        // WHEN & THEN
        generator.generate(worklist).sort(Comparator.comparing { it.first.index }).let(StepVerifier::create)
            .expectNextMatches { pair ->
                pair.first shouldBe work1
                pair.second.serial.endsWith("101")
            }.expectNextMatches { pair ->
                pair.first shouldBe work2
                pair.second.serial.endsWith( "21")
            }.verifyComplete()
    }
    should("검사코드는 유효하나 해당 기간 내에 등록된 적이 없는 신규 의뢰의 경우 연번 1번부터 생성한다") {
        // GIVEN
        val worklist = listOf(work1)

        every { workRepo.findByWorklist(worklistId) } returns Flux.fromIterable(worklist)
        every { repo.findLatest(any()) } returns Flux.empty()

        // WHEN & THEN
        generator.generate(worklist).let(StepVerifier::create).expectNextMatches { pair ->
            pair.first shouldBe work1
            pair.second.serial.endsWith("001")
        }.verifyComplete()
    }
    should("등록되지 않은 검사코드가 사용되면 예외를 반환한다") {
        val workInvalid = mockk<Work>().apply {
            every { worklist } returns worklistId
            every { index } returns 1
            every { requests } returns listOf(mockk<Request>().apply {
                every {  service.id } returns "INVALID_CODE"
            })
        }
        val worklist = listOf(workInvalid)
        every { workRepo.findByWorklist(worklistId) } returns Flux.fromIterable(worklist)
        every { repo.findLatest(any()) } returns Flux.empty()
        // WHEN & THEN
        val exception = shouldThrow<IllegalArgumentException> {
            generator.generate(worklist).collectList().block()
        }
        exception.message shouldBe "Work에 연결된 모든 의뢰정보는 동일한 서비스 그룹에 속해야 합니다: [INVALID_CODE]"
    }
})
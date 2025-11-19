package com.gcgenome.lims.usecase.serial

import com.gcgenome.lims.domain.Serial
import com.gcgenome.lims.domain.Work
import com.gcgenome.lims.usecase.work.WorkRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.security.Principal
import java.util.*

class SerialServiceTest : ShouldSpec({
    val workRepo: WorkRepository = mockk()
    val repo: SerialRepository = mockk()
    val externalService1: SerialExternalService = mockk()
    val externalService2: SerialExternalService = mockk()
    val externals = listOf(externalService1, externalService2)

    val worklistId = UUID.randomUUID()
    val principal: Principal = mockk()
    val work1 = mockk<Work>().apply {
        every { worklist } returns worklistId
        every { index } returns 1
    }
    val work2 = mockk<Work>().apply {
        every { worklist } returns worklistId
        every { index } returns 2
    }

    val generators = listOf(mockk<SerialGenerator>().apply {
        every { generate(any()) } returns Flux.just(work1 to mockk<Serial>())
    }, mockk<SerialGenerator>().apply {
        every { generate(any()) } returns Flux.just(work2 to mockk<Serial>())
    })
    val service = SerialService(workRepo, generators, repo, externals)
    beforeTest {
        clearMocks(workRepo, repo, externalService1, externalService2)
        every { repo.save(any<Serial>()) } answers { (firstArg() as Serial).toMono() }
    }
    should("작업 목록에 대한 일련 번호를 생성하고 외부 서비스에 전송한다") {
        // GIVEN
        val worklist = listOf(work1, work2)
        every { workRepo.findByWorklist(worklistId) } returns Flux.fromIterable(worklist)
        every { externalService1.publish(principal, any()) } returns Mono.empty()
        every { externalService2.publish(principal, any()) } returns Mono.empty()

        // WHEN
        service.generate(worklistId, principal).let(StepVerifier::create).verifyComplete()

        // THEN
        verify(exactly = 2) { repo.save(any()) }
        verify(exactly = worklist.size) { externalService1.publish(principal, any()) }
        verify(exactly = worklist.size) { externalService2.publish(principal, any()) }
    }

    should("한 개의 외부 서비스가 실패하는 경우 예외가 반환된다") {
        val worklist = listOf(work1, work2)
        every { workRepo.findByWorklist(worklistId) } returns Flux.fromIterable(worklist)
        every { externalService1.publish(principal, any()) } returns Mono.error(RuntimeException("External Service Error"))
        every { externalService2.publish(principal, any()) } returns Mono.empty()

        // WHEN & THEN
        val exception = shouldThrow<RuntimeException> {
            service.generate(worklistId, principal).block()
        }
        exception.message shouldBe "External Service Error"
    }
})
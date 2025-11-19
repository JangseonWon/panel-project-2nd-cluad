package com.gcgenome.lims.usecase.serial

import com.gcgenome.lims.domain.Serial
import com.gcgenome.lims.domain.Work
import com.gcgenome.lims.usecase.work.WorkRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*

@Service
class SerialService(
    private val workRepo: WorkRepository,
    private val strategies: List<SerialGenerator>,
    private val repo: SerialRepository,
    private val externals: List<SerialExternalService>
) {
    fun generate(worklistId: UUID, principal: Principal): Mono<Void> = workRepo.findByWorklist(worklistId).collectList()
        .flatMapMany { works -> applyStrategies(works, principal) }
        .then()

    private fun applyStrategies(works: List<Work>, principal: Principal): Flux<Work> = strategies.fold(Flux.fromIterable(works)) { remainingWorksFlux, strategy ->
        remainingWorksFlux.collectList().filter { it.isNotEmpty() }.flatMapMany { remainingWorks ->
            strategy.generate(remainingWorks).collectList()
                .flatMapMany { generatedPairs ->
                    processGeneratedPairs(principal, remainingWorks, generatedPairs)
                }
        }
    }

    private fun processGeneratedPairs(principal: Principal, remainingWorks: List<Work>, generatedPairs: List<Pair<Work, Serial>>): Flux<Work> {
        val processedWorks = generatedPairs.map { it.first }.toSet()
        val remainingWorkList = remainingWorks.filterNot { it in processedWorks }

        // 외부 연동 처리
        return Flux.fromIterable(generatedPairs)
            .flatMap { (_, serial) -> repo.save(serial) }
            .flatMap { publishToExternals(principal, it) }
            .thenMany(Flux.fromIterable(remainingWorkList))
    }

    private fun publishToExternals(principal: Principal, serial: Serial): Mono<Void> {
        return Flux.fromIterable(externals)
            .flatMap { it.publish(principal, serial) }
            .then()
    }

}
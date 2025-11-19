package com.gcgenome.lims.service

import com.gcgenome.lims.entity.SnvConsensualClass
import com.gcgenome.lims.entity.SnvToUpdate
import com.gcgenome.lims.repo.SnvConsensualClassRepo
import com.gcgenome.querydsl.persist
import org.slf4j.LoggerFactory
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class MarkerService(
    private val snvConsensualClassRepo: SnvConsensualClassRepo,
    template : R2dbcEntityTemplate
) {
    private val source = template.select(SnvToUpdate::class.java).from("not_published_snv").first()
    private val clazzMap = mapOf(
        "LB" to "Likely Benign",
        "B" to "Benign",
        "LP" to "Likely Pathogenic",
        "FP" to "False Positive",
        "P" to "Pathogenic",
        "VUS" to "VUS"
    )

    private val logger = LoggerFactory.getLogger(MarkerService::class.java)
    fun mark(): Mono<Boolean> {
        return source.expand { source }
            .flatMap({source.flatMap { handleUpdate(it.snvFormmated, it.clazz, it.user) }}, 1)
            .onErrorContinue{error, _ ->  logger.info("${error::class.simpleName} Happened. Ignoring") }
            .then(Mono.just(true))
    }

    @Transactional
    fun handleUpdate(snv : String, clazz : String, user : String): Mono<SnvConsensualClass> {
        logger.info("INSERT : $snv")
        return snvConsensualClassRepo.persist(
                    SnvConsensualClass(
                        snv = snv,
                        clazz = clazzMap[clazz].orEmpty(),
                        createUser = user,
                        last_modified_user = user
                    )
                )
    }
}

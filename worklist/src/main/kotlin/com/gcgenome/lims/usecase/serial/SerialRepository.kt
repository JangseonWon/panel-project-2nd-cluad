package com.gcgenome.lims.usecase.serial

import com.gcgenome.lims.domain.Serial
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.ZonedDateTime

interface SerialRepository {
    fun save(serial: Serial): Mono<Serial>
    fun findLatest(after: ZonedDateTime): Flux<Pair<String, Short>>
}
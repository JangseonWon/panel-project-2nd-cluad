package com.gcgenome.lims.usecase.serial

import com.gcgenome.lims.domain.Serial
import reactor.core.publisher.Mono
import java.security.Principal

interface SerialExternalService {
    fun publish(principal: Principal, serial: Serial): Mono<Void>
}
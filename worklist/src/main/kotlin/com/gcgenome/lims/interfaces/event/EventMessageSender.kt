package com.gcgenome.lims.interfaces.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.domain.Serial
import com.gcgenome.lims.domain.event.PanelEvent
import com.gcgenome.lims.domain.event.UpdateSerialEvent
import com.gcgenome.lims.usecase.serial.SerialExternalService
import com.github.f4b6a3.ulid.Ulid
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.security.Principal
import java.util.function.Supplier

@Component
class EventMessageSender(private val om: ObjectMapper): SerialExternalService {
    private val logger = LoggerFactory.getLogger(javaClass)
    @Bean("panel") fun panel(): Supplier<Flux<String>> = Supplier {
        buffer.asFlux().mapNotNull { msg -> om.writeValueAsString(msg) }.onErrorResume { e ->
            logger.error("Error occurred while processing buffer", e)
            Flux.empty()
        }
    }
    private val buffer: Sinks.Many<PanelEvent<*, *>> = Sinks.many().unicast().onBackpressureBuffer()
    override fun publish(principal: Principal, serial: Serial): Mono<Void> = synchronized(buffer) {
        if (buffer.tryEmitNext(UpdateSerialEvent(Ulid.fast().toUuid(), serial)).isSuccess) Mono.empty()
        else Mono.error(IllegalStateException("Failed to emit event for serial: $serial"))
    }
}
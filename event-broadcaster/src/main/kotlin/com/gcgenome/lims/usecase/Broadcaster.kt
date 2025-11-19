package com.gcgenome.lims.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.domain.event.PanelEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.Scannable
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.io.Serializable
import java.time.Duration
import java.util.*

@Service
class Broadcaster(private val om: ObjectMapper) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val sink: Sinks.Many<PanelEvent<*, *>> = Sinks.many().replay().limit(Duration.ofMillis(100))
    fun broadcast(event: String) {
        val emit = sink.tryEmitNext(om.readValue(event, PanelEventImpl::class.java))
        logger.info(emit.toString())
    }
    fun listen(): Flux<PanelEvent<*, *>> = sink.asFlux()
        .doOnCancel   {
            logger.info("Client disconnected")
        }.doOnComplete {
            logger.info("Complete:")
        }.doOnError    {
            logger.error("Error occurred: ${it.message}")
            logger.info("Subscriber count: ${sink.currentSubscriberCount()}")
        }.onErrorContinue { e, u -> logger.error("Error occurred, but continuing to wait: ${e.message}") }

    companion object {
        data class PanelEventImpl(val id: UUID, val type: PanelEvent.Type, val param: Serializable): PanelEvent<Serializable, PanelEventImpl> {
            override fun id(): UUID = id
            override fun type(): PanelEvent.Type = type
            override fun param(): Serializable = param
        }
    }
}
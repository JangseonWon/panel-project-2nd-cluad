package com.gcgenome.lims.service

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

@Configuration
class ServiceRouter(private var handler: ServiceHandler) {
    @Bean
    fun serviceRouterInstance(): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.GET("/services"), this::services)
    }

    private fun services(request: ServerRequest): Mono<ServerResponse> {
        return handler.list(request)
            .flatMap { e-> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(e) }
            .switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .onErrorResume (ServerResponse.badRequest()::bodyValue)
    }
}
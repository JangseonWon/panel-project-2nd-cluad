package com.gcgenome.lims.service

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Configuration
class Router(private val handler: Handler) {
    @Bean("com.gcgenome.lims.service.Router")
    fun router() = org.springframework.web.reactive.function.server.router {
       GET("/variants/reported", contentType(MediaType("application", "vnd.lims.v1", Charsets.UTF_8)), ::reported)
       GET("/variants/reported/{sample}", contentType(MediaType("application", "vnd.lims.v1", Charsets.UTF_8)), ::one)
    }
    private fun reported(request: ServerRequest): Mono<ServerResponse> {
        return handler.findAll().collectList()
            .flatMap(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)::bodyValue)
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
    }
    private fun one(request: ServerRequest): Mono<ServerResponse> {
        return handler.findOne(request.pathVariable("sample").toLong()).collectList()
            .flatMap(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)::bodyValue)
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
    }
}
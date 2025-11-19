package com.gcgenome.lims.service.disease

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Configuration("d")
class Router(private val handler: Handler) {
    @Bean("com.gcgenome.lims.service.disease.Router")
    fun router() = org.springframework.web.reactive.function.server.router {
        GET("/genes/{gene}/disease", contentType(MediaType("application", "vnd.lims.v1", Charsets.UTF_8)), ::disease)
    }
    private fun disease(request: ServerRequest): Mono<ServerResponse> {
        return request.pathVariable("gene").toMono().flatMapMany(handler::disease).collectList()
            .flatMap(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)::bodyValue)
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
    }
}
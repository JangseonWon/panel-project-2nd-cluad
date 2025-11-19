package com.gcgenome.lims.service

import com.gcgenome.lims.test.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.function.Function.identity
import java.util.stream.Collectors

@Configuration
class Router(private val handler: Handler) {
    @Bean("com.gcgenome.lims.service.Router")
    fun router() = org.springframework.web.reactive.function.server.router {
        GET("/services/{service}", contentType(MediaType("application", "vnd.lims.v1", Charsets.UTF_8)), ::service)

        GET("/samples/{sample}/services/{service}/interpretation", contentType(MediaType("application", "vnd.lims.v1", Charsets.UTF_8)), ::fetch)
        PUT("/samples/{sample}/services/{service}/interpretation", contentType(MediaType("application", "vnd.lims.v1+json", Charsets.UTF_8)), ::save)
        PUT("/samples/{sample}/services/{service}/auto-interpret", contentType(MediaType("application", "vnd.lims.v1+json", Charsets.UTF_8)), ::auto)
        GET("/samples/{sample}/services/{service}/negative-interpret", contentType(MediaType("application", "vnd.lims.v1", Charsets.UTF_8)), ::negative)
        DELETE("/samples/{sample}/services/{service}/interpretation", contentType(MediaType("application", "vnd.lims.v1", Charsets.UTF_8)), ::delete)
    }
    private val services: Map<String, Any> = (
            RareDiseasePanel.values() + SingleGenePanel.values() + SingleGenePanelWithMlpa.values() + GenePlusPanel.values() +
            BloodCancerPanel.values() + SolidTumorPanel.values() + NonTSO.values() + GenomeScreen.values() +  Wes.values() + WesWithSingleGene.values() +
            Sanger.values() + Des.values() +
            Mrd.values() + Hrd.values() + AlloSeq.values() + Cancerch.values() + Guardant.values() +
            `FLT3-ITD`.values() + Ballondor.values()
    ).stream().collect(Collectors.toMap(HasCode::code, identity()))
    private fun service(request: ServerRequest): Mono<ServerResponse> {
        return services[request.pathVariable("service")].toMono()
            .flatMap(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)::bodyValue)
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
    }
    private fun fetch(request: ServerRequest): Mono<ServerResponse> {
        val sample = request.pathVariable("sample").toLong()
        val service = request.pathVariable("service")
        return handler.find(sample, service).mapNotNull { it.value }
            .flatMap(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)::bodyValue)
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
    }
    private fun save(request: ServerRequest): Mono<ServerResponse> {
        val sample = request.pathVariable("sample").toLong()
        val service = request.pathVariable("service")
        return request.bodyToMono(String::class.java)
            .flatMap { handler.save(sample, service, it) }
            .then(handler.find(sample, service)).mapNotNull { it.value }
            .flatMap(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)::bodyValue)
            .doOnError { e -> e.printStackTrace() }
    }
    private fun delete(request: ServerRequest): Mono<ServerResponse> {
        return handler.delete(request.pathVariable("sample").toLong(), request.pathVariable("service"))
            .flatMap{ ServerResponse.ok().build() }
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
            .onErrorResume (DataIntegrityViolationException::class.java) { e -> ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).bodyValue("Reason phrase: ${e.message}") }
    }
    private fun auto(request: ServerRequest): Mono<ServerResponse> {
        return request.bodyToMono(Map::class.java)
            .flatMap { handler.auto(request.pathVariable("sample").toLong(), request.pathVariable("service"), it) }
            .flatMap(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)::bodyValue)
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
    }
    private fun negative(request: ServerRequest): Mono<ServerResponse> {
        val sample = request.pathVariable("sample")
        val service = request.pathVariable("service")
        return handler.negative(sample.toLong(), service)
            .flatMap(ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)::bodyValue)
    }
}
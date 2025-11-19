package com.gcgenome.lims.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.search.SearchParam
import com.gcgenome.lims.test.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Configuration
class Router(val handler: Handler, val om: ObjectMapper) {
    @Bean("com.gcgenome.lims.service.Router")
    fun router() = org.springframework.web.reactive.function.server.router {
        GET("/samples/{sample}/services/{service}/snvs", ::reported)
        GET("/samples/{sample}/services/{service}/batches/{batch}/snvs", ::reportedByBatch)
        POST("/samples/{sample}/services/{service}/batches/{batch}/{row}/snvs", ::search)
        PUT("/samples/{sample}/services/{service}/snvs/{variant}/class/{class}", ::create)
        DELETE("/samples/{sample}/services/{service}/snvs/{variant}/class", ::delete)

        GET("/samples/{sample}/analysis", ::analysis)

    }
    private fun reported(request: ServerRequest): Mono<ServerResponse> {
        val sample = request.pathVariable("sample").toLong()
        val service = request.pathVariable("service")
        val documents = handler.findReportedByRequest(sample, service)
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(documents, Document::class.java)
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
    }

    private fun reportedByBatch(request: ServerRequest): Mono<ServerResponse> {
        val sample = request.pathVariable("sample").toLong()
        val service = request.pathVariable("service")
        val batch: String = request.pathVariable("batch")
        val documents = handler.findReportedByRequestAndBatch(sample, service, batch)
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(documents, Document::class.java)
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
    }
    private fun search(request: ServerRequest): Mono<ServerResponse> {
        val sample = request.pathVariable("sample").toLong()
        val service = request.pathVariable("service")
        val batch = request.pathVariable("batch")
        val row = request.pathVariable("row").toInt()
        return request.bodyToMono(String::class.java).map{ om.readValue(it, SearchParam::class.java) }.flatMap {
            handler.findByRequest(sample, service, batch, row, it) }
            .flatMap { page->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .header("X-Total-Count", page.totalElements.toString())
                    .header("X-Total-Page", page.totalPages().toString())
                    .header("X-Current-Page", page.currentPage.toString())
                    .body(page.data, Document::class.java)
            }.switchIfEmpty(ServerResponse.status(HttpStatus.NO_CONTENT).build())
            .doOnError { e -> e.printStackTrace() }
    }
    private fun create(request: ServerRequest): Mono<ServerResponse> {
        val sample = request.pathVariable("sample").toLong()
        val service = request.pathVariable("service")
        val variant = request.pathVariable("variant")
        val classification = request.pathVariable("class")
        return ServerResponse.ok().body(handler.create(sample, service, variant, classification), String::class.java)
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
    }
    private fun delete(request: ServerRequest): Mono<ServerResponse> {
        val sample = request.pathVariable("sample").toLong()
        val service = request.pathVariable("service")
        val variant = request.pathVariable("variant")
        return ServerResponse.ok().body(handler.delete(sample, service, variant), String::class.java)
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
    }
    private fun analysis(request: ServerRequest): Mono<ServerResponse> {
        val sample = request.pathVariable("sample").toLong()
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(handler.findAnalysis(sample), String::class.java)
            .switchIfEmpty (ServerResponse.status(HttpStatus.NOT_FOUND).build())
            .doOnError { e -> e.printStackTrace() }
    }
}
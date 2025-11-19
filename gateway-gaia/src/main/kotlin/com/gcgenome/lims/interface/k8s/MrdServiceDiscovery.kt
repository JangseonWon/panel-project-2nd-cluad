package com.gcgenome.lims.`interface`.k8s

import com.gcgenome.lims.domain.Page
import com.gcgenome.lims.usecase.MenuSupplier
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class MrdServiceDiscovery(
    private val client: WebClient.Builder
): MenuSupplier {
    private val services = listOf("panel-worklist")
    override fun pages(headers: HttpHeaders): Flux<Page> = Flux.fromIterable(services)
        .flatMap { service -> routes(headers, service) }
        .flatMap { Flux.fromArray(it) }

    private fun routes(headers: HttpHeaders, service: String): Mono<Array<Page>> {
        return client.baseUrl("http://$service").build().get()
            .uri("/services")
            .headers{h->headers.forEach(h::addAll)}
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(Array<Page>::class.java)
            .timeout(Duration.ofMillis(500))
            .onErrorResume {
                it.printStackTrace()
                Mono.empty()
            }
    }
}
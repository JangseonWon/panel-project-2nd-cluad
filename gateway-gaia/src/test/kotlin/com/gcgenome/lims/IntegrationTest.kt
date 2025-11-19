package com.gcgenome.lims

import com.fasterxml.jackson.databind.ObjectMapper
import io.fabric8.kubernetes.client.KubernetesClient
import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@SpringBootTest(properties = [
    "spring.main.cloud-platform=KUBERNETES",
    "spring.cloud.kubernetes.discovery.service-labels.type=service",
    "spring.cloud.kubernetes.discovery.namespaces=test"
]) @AutoConfigureWebTestClient
class IntegrationTest(
    private val k8s: KubernetesClient,
    private val client: WebTestClient
): BehaviorSpec({

}) {
    companion object {
        private const val AUTHENTICATION = "Authentication"
        @TestConfiguration
        internal class TestWebConfig(private val objectMapper: ObjectMapper) {
            @Bean @Primary
            fun clientMock(): WebClient = WebClient.builder().exchangeFunction { request -> Mono.defer {
                if(request.cookies().containsKey(AUTHENTICATION)) {
                    val svc = request.url().host
                    Mono.just(
                        ClientResponse.create(OK).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(Menu(svc)))
                        .build())
                } else Mono.just(ClientResponse.create(UNAUTHORIZED).build())
            }}.build()
        }
        data class Menu(val title: String)
    }
}
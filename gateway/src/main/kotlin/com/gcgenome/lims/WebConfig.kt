package com.gcgenome.lims

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import java.util.concurrent.TimeUnit

@Configuration
@EnableAsync
@EnableWebFlux
class WebConfig(private val objectMapper: ObjectMapper) : WebFluxConfigurer {
    @Bean
    @LoadBalanced
    fun clientBuilder(): WebClient.Builder = WebClient.builder().exchangeStrategies(
        ExchangeStrategies.builder()
            .codecs {
                it.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper))
                it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper))
            }.build())
    @Bean
    fun addViewControllers(): WebFilter = WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
        if (exchange.request.uri.path == "/") chain.filter(exchange.mutate().request(exchange.request.mutate().path("/index.html").build()).build())
        else chain.filter(exchange)
    }
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/", "file:/data/lims/static/panel-service")
            .setCacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES))
            .resourceChain(false)
    }
}
package com.gcgenome.lims

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuditFilter: AbstractGatewayFilterFactory<Any>() {
    val logger : Logger = LoggerFactory.getLogger(AuditFilter::class.java)
    override fun apply(config: Any?): OrderedGatewayFilter =
        OrderedGatewayFilter({ exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val request = exchange.request
            val user = request.headers.getFirst("X-USER-ID")
            if(user==null) chain.filter(exchange)
            else {
                val uri = request.uri.toString()
                Mono.fromRunnable {
                    val resp = exchange.response
                    logger.info(uri + " " + resp.statusCode + " " + resp.headers)
                }
            }
        }, OrderedGatewayFilter.LOWEST_PRECEDENCE)
}
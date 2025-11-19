package com.gcgenome.lims

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.route.Route
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils.isAlreadyRouted
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils.setAlreadyRouted
import org.springframework.core.Ordered
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.io.File


@Component
class FileRoutingFilter: GlobalFilter, Ordered {
    override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val route = exchange.getAttribute<Any>(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR) as Route
        val routeUri = route.uri
        val scheme = routeUri.scheme
        if (isAlreadyRouted(exchange) || ("file" != scheme && "classpath"!=scheme)) return chain.filter(exchange)
        val request = exchange.request
        val response = exchange.response
        val file = File("${routeUri.path}${request.uri.path}")
        return if (!file.exists()) chain.filter(exchange)
        else {
            setAlreadyRouted(exchange)
            response.writeWith(DataBufferUtils.read(FileSystemResource(file.toPath()), response.bufferFactory(), 1024))
        }
    }
}
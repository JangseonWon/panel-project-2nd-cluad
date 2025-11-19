package com.gcgenome.lims.`interface`.api

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class UserIdAuthenticationConverter: ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val request = exchange.request
        val authentication = request.headers.getFirst(USER_ID_HEADER) ?.let { UserIdAuthenticationToken(it) }
        return Mono.justOrEmpty(authentication)
    }
    companion object {
        const val USER_ID_HEADER = "X-USER-ID"
        private class UserIdAuthenticationToken(private val id: String): AbstractAuthenticationToken(emptySet()) {
            override fun getCredentials(): String = id
            override fun getPrincipal(): String = id
        }
    }
}
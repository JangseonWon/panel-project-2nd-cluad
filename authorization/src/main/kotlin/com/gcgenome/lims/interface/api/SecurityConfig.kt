package com.gcgenome.lims.`interface`.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(private val userIdAuthenticationConverter: UserIdAuthenticationConverter, userAuthenticationManager: UserAuthenticationManager) {
    private val authenticationWebFilter = AuthenticationWebFilter(userAuthenticationManager)
    @Bean fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http {
        csrf { disable() }
        httpBasic { disable() }
        formLogin { disable() }
        headers { frameOptions { mode = XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN } }
        authorizeExchange {
            authorize("/actuator/health/**", permitAll)
            authorize(anyExchange, authenticated)
        }
        authenticationWebFilter.setServerAuthenticationConverter(userIdAuthenticationConverter)
        addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
    }
}
package com.gcgenome.lims

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Duration

@Configuration
@Order(2)
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@EnableR2dbcAuditing
class SecurityConfig (
    private val securityContextRepository: SecurityContextRepository
) {
    @Bean
    fun resourceFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.cors().and()
            .httpBasic().disable()
            .formLogin().disable()
            .csrf().disable()
            .headers().frameOptions().mode(XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN).and()
            .exceptionHandling()
            .authenticationEntryPoint { swe: ServerWebExchange, _: AuthenticationException ->
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED }
            }.accessDeniedHandler { swe: ServerWebExchange, _: AccessDeniedException ->
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN }
            }.and().securityContextRepository(securityContextRepository)
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .anyExchange().authenticated()
            .and().build()
    }

    @Bean
    fun auditorProvider(): ReactiveAuditorAware<String> {
        return ReactiveAuditorAware {
            ReactiveSecurityContextHolder.getContext()
                .timeout(Duration.ofSeconds(1))
                .map { obj: SecurityContext -> obj.authentication }
                .filter { obj: Authentication -> obj.isAuthenticated }
                .map { obj: Authentication -> obj.principal }
                .map { obj: Any? -> String::class.java.cast(obj) }
        }
    }
}
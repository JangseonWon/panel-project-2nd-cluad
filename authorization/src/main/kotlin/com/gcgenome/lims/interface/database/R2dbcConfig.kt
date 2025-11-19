package com.gcgenome.lims.`interface`.database

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.time.Duration
import java.util.*

@ConditionalOnMissingBean(ReactiveAuditorAware::class)
@Configuration
@EnableR2dbcRepositories
@EnableTransactionManagement
@EnableR2dbcAuditing
class R2dbcConfig {
    @Bean fun auditorProvider(): ReactiveAuditorAware<String> = ReactiveAuditorAware {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"))
        ReactiveSecurityContextHolder.getContext()
            .timeout(Duration.ofSeconds(1))
            .map { obj: SecurityContext -> obj.authentication }
            .filter { obj: Authentication -> obj.isAuthenticated }
            .map { obj: Authentication -> obj.principal as String }
    }
}
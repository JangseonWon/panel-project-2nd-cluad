package com.gcgenome.lims.`interface`.api

import com.gcgenome.lims.domain.AuthenticatedUser
import com.gcgenome.lims.usecase.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserAuthenticationManager(private val repo: UserRepository): ReactiveAuthenticationManager {
    private val log = LoggerFactory.getLogger(javaClass)
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        if(authentication==null) return Mono.empty()
        val id = authentication.principal as String
        return repo.findById(id).doOnError { log.error("Error during authentication", it) }
            .filter(AuthenticatedUser::activated)
            .switchIfEmpty(Mono.error(UsernameNotFoundException("User not found")))
            .map { user -> UserAuthentication(user).apply { isAuthenticated = true } }
    }
}
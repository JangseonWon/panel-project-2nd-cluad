package com.gcgenome.lims.usecase

import com.gcgenome.lims.domain.AuthenticatedUser
import reactor.core.publisher.Mono

interface UserRepository {
    fun findById(id: String): Mono<AuthenticatedUser>
}
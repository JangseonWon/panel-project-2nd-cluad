package com.gcgenome.lims

import com.gcgenome.lims.entity.User
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserDao(private val repo: UserRepository) {
    fun findById(id: String): Mono<User> = repo.findById(id)
}
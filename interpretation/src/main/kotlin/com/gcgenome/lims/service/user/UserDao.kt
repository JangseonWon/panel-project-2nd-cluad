package com.gcgenome.lims.service.user

import com.gcgenome.lims.entity.QUser
import com.gcgenome.lims.entity.User
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserDao(private val repo: UserRepository) {
    fun findById(id: String): Mono<User> = repo.query {
        it.select(QUser.user).from(QUser.user).where(QUser.user.id.eq(id))
    }.one()
}
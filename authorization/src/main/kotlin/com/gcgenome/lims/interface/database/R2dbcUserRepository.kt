package com.gcgenome.lims.`interface`.database

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.domain.AuthenticatedUser
import com.gcgenome.lims.`interface`.database.UserEntity.Companion.Role.A
import com.gcgenome.lims.`interface`.database.UserEntity.Companion.Role.M
import com.gcgenome.lims.`interface`.database.UserEntity.Companion.State.ACTIVATE
import com.gcgenome.lims.usecase.UserRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Repository
class R2dbcUserRepository(private val template: R2dbcEntityTemplate, private val om: ObjectMapper): UserRepository {
    @Transactional(readOnly = true)
    override fun findById(id: String): Mono<AuthenticatedUser> = template
        .selectOne(query(where("id").`is`(id)), UserEntity::class.java)
        .map(this::toDomain)
    private fun toDomain(entity: UserEntity): AuthenticatedUser = entity.let {
        AuthenticatedUser(id = it.id, name = it.name,
            manager= when (it.role) {
                M, A -> true
                else -> false
            }, activated=it.state==ACTIVATE)
    }
}
package com.gcgenome.lims.`interface`.api

import com.gcgenome.lims.domain.AuthenticatedUser
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserAuthentication (
    val id: String,
    val roles: List<String>
): AbstractAuthenticationToken(roles.map { SimpleGrantedAuthority(it) }) {
    constructor(user: AuthenticatedUser): this(user.id, when {
        user.activated.not() -> listOf("ROLE_ANONYMOUS")
        user.manager -> listOf("ROLE_MANAGER", "ROLE_USER")
        else -> listOf("ROLE_USER")
    })
    override fun getName(): String = id
    override fun getCredentials(): String = id
    override fun getPrincipal(): String = id
}
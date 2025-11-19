package com.gcgenome.lims.`interface`.database

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table

@Table("public.user")
data class UserEntity (
    @Id private val id: String,
    var department: String?,
    var name: String,
    var email: String?,
    var state: State,
    var role: Role,
): Persistable<String> {
    override fun getId(): String = id
    override fun isNew(): Boolean = false
    companion object {
        enum class State {
            ACTIVATE, INACTIVATE,
        }
        enum class Role {
            U, M, A
        }
    }
}
package com.gcgenome.lims.domain

@JvmRecord
data class AuthenticatedUser (
    val id: String,
    val name: String,
    val manager: Boolean,
    val activated: Boolean
)
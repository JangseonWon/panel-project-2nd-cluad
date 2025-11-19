package com.gcgenome.lims.entity;

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("public.user")
data class User(
    @Id val id: String,
    val name: String
)
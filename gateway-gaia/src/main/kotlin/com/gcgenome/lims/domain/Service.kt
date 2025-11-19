package com.gcgenome.lims.domain

@JvmRecord
data class Service(val title: String, val order: String, val prefix: String, val children: List<Page>)
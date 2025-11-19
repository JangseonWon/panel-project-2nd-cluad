package com.gcgenome.lims.domain

import com.fasterxml.jackson.annotation.JsonProperty

@JvmRecord
data class Page (
    val icon: String,
    @JsonProperty("icon_type")
    val iconType: String?,
    val title: String,
    val uri: String,
    val order: String
)
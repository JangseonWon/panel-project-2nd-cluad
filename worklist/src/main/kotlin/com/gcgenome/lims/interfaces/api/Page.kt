package com.gcgenome.lims.interfaces.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Page (
    val icon: String,
    val title: String,
    val uri: String,
    val order: String,
    @JsonProperty("icon_type")
    val iconType: String? = null
)
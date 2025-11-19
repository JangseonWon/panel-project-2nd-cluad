package com.gcgenome.lims.search

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*

data class SearchParam (
    val page: Int?,
    val limit: Int?,
    @JsonProperty("sort_by")
    val sortBy: String? = null,
    val asc: Boolean? = false
): Serializable {
    val filters: MutableList<Filter> = mutableListOf()
    constructor(@JsonProperty("page") page: Int?, @JsonProperty("limit")limit: Int?, @JsonProperty("sort_by") sortBy: String?, asc: Boolean?, filters: List<Filter>?): this(page, limit, sortBy, asc) {
        filters?.let { this.filters.addAll(it) }
    }
    companion object {
        data class Filter(
            val key: String?,
            val value: String?
        ): Serializable
    }
}
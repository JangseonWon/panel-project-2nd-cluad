package com.gcgenome.lims.search

import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import java.util.function.Function
import kotlin.math.ceil

data class PageReactive<T> (
    val totalElements: Long,
    val pageSize: Int?,
    val currentPage: Int,
    val data: Flux<T>
) {
    fun totalPages(): Long? = if (pageSize == null) null else ceil(totalElements / pageSize.toDouble()).toLong()
    fun <F> map(func: Function<T, F>): PageReactive<F> = PageReactive(totalElements, pageSize, currentPage, data.map(func))
    fun <F> flatMap(func: Function<T, Publisher<out F>>): PageReactive<F> = PageReactive(totalElements, pageSize, currentPage, data.flatMap(func))
}


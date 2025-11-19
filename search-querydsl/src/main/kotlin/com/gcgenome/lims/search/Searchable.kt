package com.gcgenome.lims.search

import com.infobip.spring.data.r2dbc.QuerydslR2dbcFragment
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Expression
import com.querydsl.sql.SQLQuery
import reactor.core.publisher.Mono

abstract class Searchable<E, V> (
    private val repo: QuerydslR2dbcFragment<E>
): Filterable<E> {
    private fun predicate(param: SearchParam): BooleanBuilder {
        val builder = BooleanBuilder()
        param.filters.forEach {
            val predicate = predicate(it.key, it.value)
            if(predicate!=null) builder.and(predicate)
        }
        return builder
    }
    abstract fun <T> from(query: SQLQuery<T>, param: SearchParam): SQLQuery<T>
    abstract fun projection(): Expression<V>
    fun search(param: SearchParam) : Mono<PageReactive<V>> {
        val predicates = predicate(param)
        val flux = repo.query {
            if(param.sortBy!=null) {
                val expression = column(param.sortBy!!)
                it.orderBy(if(param.asc!=null && param.asc!!) expression.asc() else expression.desc())
            }
            if(param.limit!=null && param.page!=null) it.limit(param.limit!!.toLong()).offset(param.page!!*param.limit!!.toLong())
            from(it.select(projection()), param).where(predicates)
        }.all()
        val count = repo.query { from(it.select(pk().countDistinct()), param).where(predicates) }
        return count.one().map { PageReactive(it, param.limit, param.page?:0, flux) }
    }
}
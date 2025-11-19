package com.gcgenome.lims.search

import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.ComparableExpressionBase
import com.querydsl.core.types.dsl.SimpleExpression

interface Filterable<E> {
    fun pk(): SimpleExpression<*>
    fun column(key: String): ComparableExpressionBase<*>
    fun predicate(key: String?, value: String?): Predicate?
}
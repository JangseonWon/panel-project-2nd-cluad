package com.gcgenome.lims.elasticsearch

import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.Field
import org.springframework.data.elasticsearch.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

// spring data elasticsearch의 criteria생성을 안전하게 하는 유틸 클래스
// Criteria 생성 과정을 operator, parameter 두 단계로 나눈 state machine으로 생각한다.
// operator는 all, count, search 등 연산을 지원한다. 필드를 가져오는 get을 호출하면 parameter로 상태가 전환된다.
// 필드를 가져오는 연산은 가져온 필드의 구체적인 조건이 주어져야 의미를 가진다. parameter 상태는 그 조건을 요구하는 상태임.
// 조건이 주어져야 종단 연산을 수행하는 operator로 상태가 전환된다.
class ElasticsearchDao(private val em: ReactiveElasticsearchTemplate) {
    fun <T> find(index: IndexCoordinates, id: String, clazz: Class<T>): Mono<T> = em.get(id, clazz, index)
    open class CriteriaBuilder internal constructor(internal val index: IndexCoordinates, internal var criteria: Criteria?) {
        override fun toString(): String = criteria.toString()
    }
    class CriteriaBuilderOperator(
        private val em: ReactiveElasticsearchTemplate,
        index: IndexCoordinates, criteria: Criteria? = null
    ) : CriteriaBuilder(index, criteria) {
        fun and(builder: CriteriaBuilderOperator): CriteriaBuilderOperator {
            if(this == builder) return this
            val c = builder.criteria
            if (c != null) this.criteria = this.criteria?.and(c) ?: c
            return this
        }
        fun or(builder: CriteriaBuilderOperator): CriteriaBuilderOperator {
            if(this == builder) return this
            val c = builder.criteria
            if (c != null) this.criteria = this.criteria?.or(c) ?: c
            return this
        }
        operator fun get(field: Field) = CriteriaBuilderParameter(em, index, (criteria ?: Criteria(field)).and(field))
        operator fun get(field: String) = CriteriaBuilderParameter(em, index, (criteria ?: Criteria(field)).and(field))

        fun all() = CriteriaBuilderParameter(em, index, Criteria())
        fun count(): Mono<Long> = em.count(criteria?.let { CriteriaQuery(it) } ?: Query.findAll(), Any::class.java, index)
        fun search(): Flux<SearchHit<Any>> = em.search(criteria?.let { CriteriaQuery(it) } ?: Query.findAll(), Any::class.java, index)
    }
    class CriteriaBuilderParameter internal constructor (
        private val em: ReactiveElasticsearchTemplate,
        index: IndexCoordinates, private val criteria_: Criteria
    ) : CriteriaBuilder(index, criteria_) {
        fun contains(value: String)     = CriteriaBuilderOperator(em, index, criteria_.contains(value))
        fun `in`(vararg values: String) = CriteriaBuilderOperator(em, index, criteria_.`in`(*values))
        fun notIn(vararg values: String)= CriteriaBuilderOperator(em, index, criteria_.notIn(*values))
        val isEmpty get()               = CriteriaBuilderOperator(em, index, criteria_.not().exists())
        val isNotEmpty get()            = CriteriaBuilderOperator(em, index, criteria_.exists())
        fun `is`(value: String)         = CriteriaBuilderOperator(em, index, criteria_.`is`(value))
        fun equal(n: Number)            = CriteriaBuilderOperator(em, index, criteria_.`is`(n))
        fun less(n: Number)             = CriteriaBuilderOperator(em, index, criteria_.lessThan(n))
        fun lessOrEquals(n: Number)     = CriteriaBuilderOperator(em, index, criteria_.lessThanEqual(n))
        fun greater(n: Number)          = CriteriaBuilderOperator(em, index, criteria_.greaterThan(n))
        fun greaterOrEquals(n: Number)  = CriteriaBuilderOperator(em, index, criteria_.greaterThanEqual(n))
        fun matches(values: Any)        = CriteriaBuilderOperator(em, index, criteria_.matches(values))
    }
}
package com.gcgenome.lims.elasticsearch

import com.gcgenome.lims.search.PageReactive
import com.gcgenome.lims.search.SearchParam
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.document.Document
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class SearchableElasticsearchDao(private val em: ReactiveElasticsearchTemplate) {
    private val dao =  ElasticsearchDao(em)
    fun IndexCoordinates.findById(id: String): Mono<Document> = dao.find(this, id, Document::class.java)
    fun IndexCoordinates.count(param: SearchParam): Mono<Long> = param.toCriteriaBuilder(this).count()
    fun IndexCoordinates.data(param: SearchParam): Flux<SearchHit<Document>> = param.toCriteriaBuilder(this).pageable(param.toPageable()).search()
    fun IndexCoordinates.search(param: SearchParam): Mono<PageReactive<Document>> {
        val count = this.count(param)
        val data = this.data(param).map { hit->hit.content }
        return count.map { cnt -> PageReactive(cnt, param.page, param.limit!!, data) }
    }
    private fun SearchParam.toCriteriaBuilder(index: IndexCoordinates): ElasticsearchDao.CriteriaBuilderOperator = filters.stream().collect(
        { ElasticsearchDao.CriteriaBuilderOperator(em, index) },
        { c, f -> c.and(f) },
        ElasticsearchDao.CriteriaBuilderOperator::and)
    private fun SearchParam.toPageable(): Pageable {
        assert(sortBy!=null && asc!=null && page!=null && limit!=null)
        val sort = Sort.by(if (this.asc!!) Sort.Direction.ASC else Sort.Direction.DESC, sortBy)
        return PageRequest.of(page!!, limit!!, sort)
    }
    // Where chaining을 가능하게 하고, 페이징 종단 연산을 수행하는 pageable로 상태 전환 기능을 추가함
    abstract fun ElasticsearchDao.CriteriaBuilderOperator.toCriteria(filter: SearchParam.Companion.Filter): ElasticsearchDao.CriteriaBuilderOperator
    private fun ElasticsearchDao.CriteriaBuilderOperator.and(filter: SearchParam.Companion.Filter): ElasticsearchDao.CriteriaBuilderOperator = and(toCriteria(filter))
    private fun ElasticsearchDao.CriteriaBuilderOperator.pageable(pageable: Pageable) = CriteriaBuilderPageable(em, index, this, pageable)
    
    // 페이징 검색 종단 연산 담당
    class CriteriaBuilderPageable internal constructor (
        private val em: ReactiveElasticsearchTemplate,
        index: IndexCoordinates,
        builder: ElasticsearchDao.CriteriaBuilder,
        private val pageable: Pageable
    ) : ElasticsearchDao.CriteriaBuilder(index, builder.criteria) {
        fun search(): Flux<SearchHit<Document>> {
            val query = criteria?.let { CriteriaQuery(it, pageable) } ?: Query.findAll().setPageable(pageable)
            return em.search(query, Document::class.java, index)
        }
    }
}
package com.gcgenome.lims.interfaces.database.worklist

import com.gcgenome.lims.infra.database.R2dbcSearchable
import com.gcgenome.lims.domain.*
import com.gcgenome.lims.usecase.worklist.WorklistRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.sql.SqlIdentifier
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Repository
class R2dbcWorklistRepository (private val template: R2dbcEntityTemplate): WorklistRepository,
    R2dbcSearchable<WorklistEntity, Worklist> {
    @Transactional(readOnly = true)
    override fun search(param: Search): Mono<Page<Worklist>> {
        val pageNumber = param.page ?: 0
        val pageSize = param.limit ?: 10
        val sortBy = param.sortBy?.let(::property) ?: "create_at"
        val sort = (param.asc?.let { if(it) Sort.Order.asc(sortBy) else Sort.Order.desc(sortBy) } ?: Sort.Order.desc(sortBy)).let { Sort.by(it) }
        val pageable = PageRequest.of(pageNumber, pageSize, sort)
        return template.search(SqlIdentifier.unquoted("panel.worklist"), param.filters, WorklistEntity::class.java, pageable).mapNotNull { page ->
            if(page.content.isEmpty()) null
            else PageImpl(page.content.map(::map), page.pageable, page.totalElements)
        }
    }
    override fun map(entity: WorklistEntity): Worklist = Worklist(
        id=entity.id,
        createAt = entity.createAt, createBy = entity.createUserName,
        lastModifyAt = entity.lastModifyAt,
        title=entity.title, status=entity.status, remark=entity.remark, domain=entity.domain,
        sampleCount = entity.sampleCount
    )
    private fun property(name: String): String? = null
    override fun R2dbcEntityTemplate.predicate(key: String, value: String): Criteria {
        if(key == "domain") return where("domain").`in`(value.split(",")).ignoreCase(true)
        else if(key == "without_disposal") return where("status").not("DISPOSAL")
        val property = property(key) ?: return Criteria.empty()
        return where(property).`is`(value).ignoreCase(true)
    }
}
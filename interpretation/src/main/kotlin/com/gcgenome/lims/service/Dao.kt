package com.gcgenome.lims.service

import com.gcgenome.lims.entity.QInterpretation.interpretation
import com.gcgenome.lims.entity.QUser
import com.gcgenome.lims.projection.Interpretation
import com.querydsl.core.types.Projections.constructor
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.sql.SQLQuery
import io.r2dbc.postgresql.codec.Json
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class Dao(private val repo: Repository) {
    private val createBy = QUser("Creator")
    private val modifyBy = QUser("Modifier")
    private fun select(query: SQLQuery<*>): SQLQuery<Interpretation.Companion.InterpretationBuilder> {
        return query.select(
            constructor(
                Interpretation.Companion.InterpretationBuilder::class.java,
                interpretation.sample,
                interpretation.service,
                createBy.id.`as`("creatorId"),
                createBy.name.`as`("creator"),
                interpretation.createAt.`as`("createAt"),
                interpretation.lastModifyBy.`as`("lastModifierId"),
                modifyBy.name.`as`("lastModifier"),
                interpretation.lastModifyAt.`as`("lastModifyAt"),
                interpretation.json.`as`("json")
            )
        ).from(interpretation)
            .leftJoin(createBy).on(createBy.id.eq(interpretation.createBy))
            .leftJoin(modifyBy).on(modifyBy.id.eq(interpretation.lastModifyBy))
    }
    fun findById(sample: Long, service: String): Mono<Interpretation> {
        return repo.query {
            select(it).where(interpretation.sample.eq(sample).and(interpretation.service.eq(service)))
        }.one().map(Interpretation.Companion.InterpretationBuilder::build)
    }
    fun delete(sample: Long, service: String): Mono<Boolean> {
        return repo.deleteWhere(interpretation.sample.eq(sample).and(interpretation.service.eq(service)))
            .flatMap {
                when(it) {
                    0L -> Mono.empty()
                    1L -> Mono.just(true)
                    else -> Mono.error(RuntimeException())
                }
            }
    }
    fun merge(sample: Long, service: String, json: String): Mono<Void> {
        return repo.query { it.select(
            constructor(com.gcgenome.lims.entity.Interpretation::class.java,
                interpretation.sample,
                interpretation.service,
                interpretation.createBy,
                interpretation.createAt,
                interpretation.lastModifyBy,
                interpretation.lastModifyAt,
                interpretation.json)
        ).from(interpretation).where(interpretation.sample.eq(sample).and(interpretation.service.eq(service))) }
            .one().switchIfEmpty(Mono.just(com.gcgenome.lims.entity.Interpretation(sample, service)))
            .flatMap { repo.merge(it, json) }
    }
    private fun Repository.merge(entity: com.gcgenome.lims.entity.Interpretation, json: String?): Mono<Void> {
        return if(entity.isNew) repo.save(entity.apply { if(json!=null) entity.json = Json.of(json) }).then()
        else update {
            Expressions.stringPath(interpretation.json.metadata)
            if(json!=null) it.set(Expressions.stringPath(interpretation.json.metadata), json)
            else it.setNull(interpretation.json)
            it.where(interpretation.sample.eq(entity.sample).and(interpretation.service.eq(entity.service)))
        }.then()
    }
}
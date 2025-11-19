package com.gcgenome.lims.interfaces.database.serial

import com.gcgenome.lims.domain.Serial
import com.gcgenome.lims.interfaces.database.work.WorkEntity
import com.gcgenome.lims.usecase.serial.SerialRepository
import com.github.f4b6a3.ulid.Ulid
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.ZonedDateTime
import java.util.*

@Repository
class R2DbcSerialRepository(private val template: R2dbcEntityTemplate): SerialRepository {
    @Transactional
    override fun save(serial: Serial): Mono<Serial> = existsWork(serial.worklist, serial.index).filter { it }
        .switchIfEmpty(Mono.error(RuntimeException("F~~~~")))
        .thenReturn(
            SerialEntity(
            id = Ulid.fast().toUuid(),
            worklist = serial.worklist,
            index = serial.index,
            infix = serial.infix,
            idx = serial.idx,
            serial = serial.serial
        )
        ).flatMap(template::insert)
        .map(::map)
    private fun existsWork(worklist: UUID, index: Short): Mono<Boolean> = template.exists(query(
        where("worklist").`is`(worklist).
        and("index").`is`(index)
    ), WorkEntity::class.java)
    private fun map(sample: SerialEntity): Serial = Serial (
        id = sample.id,
        worklist = sample.worklist,
        index = sample.index,
        serial = sample.serial,
        infix = sample.infix,
        idx = sample.idx
    )
    override fun findLatest(after: ZonedDateTime): Flux<Pair<String, Short>> = template.databaseClient.sql("SELECT infix FROM panel.serial WHERE last=true GROUP BY infix").fetch().all().flatMap { row->
        val infix = row["infix"] as String
        findLatest(infix, after).map { cnt -> infix to cnt }
    }
    private fun findLatest(infix: String, startWith: ZonedDateTime): Mono<Short> = template.selectOne(
        query(where("serial").isNotNull
            .and("last").isTrue
            .and("infix").`is`(infix)
            .and("last_modify_at").greaterThan(startWith))
            .sort(Sort.by(Sort.Direction.DESC, "idx"))
            .limit(1), SerialEntity::class.java
    ).mapNotNull { entity -> entity.idx }
}
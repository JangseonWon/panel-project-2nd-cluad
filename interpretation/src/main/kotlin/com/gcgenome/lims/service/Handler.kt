package com.gcgenome.lims.service

import com.gcgenome.lims.interpretable.Interpretable
import com.gcgenome.lims.projection.Interpretation
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class Handler(val dao: Dao, val interpreter: List<Interpretable>) {
    @Transactional(readOnly = true)
    fun find(sample: Long, service: String): Mono<Interpretation> = dao.findById(sample, service)

    @Transactional
    fun save(sample: Long, service: String, param: Any): Mono<Void> = dao.merge(sample, service, param.toString())

    @Transactional
    fun delete(sample: Long, service: String): Mono<Boolean> = dao.delete(sample, service)

    @Transactional(readOnly = true)
    fun auto(sample: Long, service: String, param: Map<*, *>): Mono<*> = interpreter.stream().filter { it.chk(sample, service) }.findFirst().map {  it.interpret(sample, service, param)}.orElse(Mono.empty<Any>())

    @Transactional(readOnly = true)
    fun negative(sample: Long, service: String): Mono<*> = interpreter.stream().filter { it.chk(sample, service) }.findFirst().map { it.negative(sample, service) }.orElse(Mono.empty<Any>())
}
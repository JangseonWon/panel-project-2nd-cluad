package com.gcgenome.lims.interpretable

import reactor.core.publisher.Mono

interface Interpretable {
    fun chk(sample: Long, service: String): Boolean
    fun interpret(sample: Long, service: String, param: Map<*, *>): Mono<*>
    fun negative(sample: Long, service: String): Mono<*>
}
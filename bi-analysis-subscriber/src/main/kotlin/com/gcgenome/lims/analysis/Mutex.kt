package com.gcgenome.lims.analysis

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Service
class Mutex {
    private val locks: MutableMap<String, Any> = ConcurrentHashMap()
    fun acquire(name: String): Mono<Any> = synchronized(locks) {
        if(locks.containsKey(name)) Mono.delay(Duration.ofSeconds(5)).then(Mono.defer{acquire(name)})
        else Mono.just(locks.computeIfAbsent(name) { Any() })
    }
    fun release(name: String) = locks.remove(name)
}
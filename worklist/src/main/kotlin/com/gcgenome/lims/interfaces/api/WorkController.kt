package com.gcgenome.lims.interfaces.api

import com.gcgenome.lims.domain.Work
import com.gcgenome.lims.usecase.work.WorkService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
class WorkController(private val svc: WorkService) {
    @GetMapping("/worklists/{id}", produces = ["application/vnd.gcgenome.lims.v1+json"])
    fun findWorksByWorklist(@PathVariable id: UUID): Flux<Work> = svc.findByWorklist(id)

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): Mono<String> {
        return Mono.justOrEmpty(ex.localizedMessage)
    }
}
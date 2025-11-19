package com.gcgenome.lims.interfaces.api

import com.gcgenome.lims.domain.*
import com.gcgenome.lims.usecase.worklist.WorklistSearchService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
class WorklistController(private val svc: WorklistSearchService) {
    @GetMapping("/worklists", produces = ["application/vnd.gcgenome.lims.v1+json"])
    fun findWorklists(param: Search): Mono<Page<Worklist>> = svc.search(param)

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): Mono<String> {
        return Mono.justOrEmpty(ex.localizedMessage)
    }
}
package com.gcgenome.lims.`interface`.api

import com.gcgenome.lims.domain.Service
import com.gcgenome.lims.usecase.MenuService
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping
class MenuController(private val svc: MenuService) {
    @GetMapping(value = ["/services"])
    @ResponseStatus(HttpStatus.OK)
    fun services(request: ServerHttpRequest): Mono<Service> = svc.services(request.headers)
}
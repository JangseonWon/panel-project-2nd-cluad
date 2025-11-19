package com.gcgenome.lims.interfaces.api

import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
class ServiceController {
    @GetMapping("/services")
    fun getService(): Flux<Page> = Flux.just(Page(icon = "fa-braille", title = "워크리스트", uri = "/panel-service/panel-worklist.html", order = "1"))
}
package com.gcgenome.lims.interfaces.api

import com.gcgenome.lims.usecase.serial.SerialService
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*

@RestController
class SerialController(private val svc: SerialService) {
    @PutMapping("/worklists/{worklist}/generate-serials")
    fun update(@PathVariable worklist: UUID, principal: Principal): Mono<Void> = svc.generate(worklist, principal)

    @ExceptionHandler(DuplicateKeyException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDuplicateKeyException(ex: DuplicateKeyException): Mono<String> {
        return Mono.justOrEmpty(ex.localizedMessage)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): Mono<String> {
        ex.printStackTrace()
        return Mono.justOrEmpty(ex.localizedMessage)
    }
}
package com.gcgenome.lims.usecase

import com.gcgenome.lims.domain.Page
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Flux

interface MenuSupplier {
    fun pages(headers: HttpHeaders): Flux<Page>
}
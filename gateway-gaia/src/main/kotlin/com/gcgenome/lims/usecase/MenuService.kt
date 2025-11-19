package com.gcgenome.lims.usecase

import com.gcgenome.lims.domain.Service
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono
import java.util.*

@org.springframework.stereotype.Service
class MenuService(private val supplier: MenuSupplier) {
    fun services(headers: HttpHeaders): Mono<Service> = supplier.pages(headers).filter(Objects::nonNull)
        .collectSortedList(compareBy(nullsLast()){ it.order })
        .map {
            Service(
                title="패널검사",
                order="5",
                prefix="/panel-service",
                children = it
            )
        }
}
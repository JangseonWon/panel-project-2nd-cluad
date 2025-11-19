package com.gcgenome.lims.usecase.work

import com.gcgenome.lims.domain.Work
import reactor.core.publisher.Flux
import java.util.*

interface WorkRepository {
    fun findByWorklist(id: UUID): Flux<Work>
}
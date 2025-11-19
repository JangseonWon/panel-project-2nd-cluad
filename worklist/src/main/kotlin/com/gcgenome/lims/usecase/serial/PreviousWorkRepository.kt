package com.gcgenome.lims.usecase.serial

import com.gcgenome.lims.domain.Work
import reactor.core.publisher.Flux

interface PreviousWorkRepository {
    fun findPrevious(works: List<Work>): Flux<Work>
}
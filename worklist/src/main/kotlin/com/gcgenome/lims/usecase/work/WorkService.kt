package com.gcgenome.lims.usecase.work

import com.gcgenome.lims.domain.Work
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.*

@Service
class WorkService(private val repo: WorkRepository) {
    fun findByWorklist(id: UUID): Flux<Work> = repo.findByWorklist(id)
}
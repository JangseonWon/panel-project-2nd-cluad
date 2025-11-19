package com.gcgenome.lims.analysis.repository

import com.gcgenome.lims.analysis.entity.PanelType
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

interface PanelTypeRepository : ReactiveCrudRepository<PanelType, Int> {
    fun getPanelTypeByEffectiveDateLessThanEqualAndExpirationDateGreaterThan(effectiveDate: LocalDateTime, expirationDate: LocalDateTime): Flux<PanelType>
}
package com.gcgenome.lims.analysis

import com.gcgenome.lims.analysis.entity.PanelType
import com.gcgenome.lims.analysis.repository.PanelTypeRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class PanelTypeManager(val repo: PanelTypeRepository) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val map = ConcurrentHashMap<String, List<PanelType>>()
    @Scheduled(fixedDelay = 1000 * 60 * 60)
    fun update() {
        val now = LocalDateTime.now()
        // logger.info("PanelType synchronization started.")
        repo.getPanelTypeByEffectiveDateLessThanEqualAndExpirationDateGreaterThan(now, now)
            .groupBy { i->i.panel }
            .flatMap { flux -> flux.collectList().map { Pair(flux.key(), it) } }
            .doOnNext { pair -> map[pair.first] = pair.second }
            .count()//.doOnNext{ logger.info("PanelType synchronized. total panel count :$it") }
            .subscribe()
    }
    fun subpanels(panel: String) = map[panel]
}
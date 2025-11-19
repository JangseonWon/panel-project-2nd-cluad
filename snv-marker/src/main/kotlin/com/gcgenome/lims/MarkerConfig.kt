package com.gcgenome.lims

import com.gcgenome.lims.service.MarkerService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.net.InetAddress
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit


@Configuration
class MarkerConfig(private val markerService : MarkerService) {
    private val logger = LoggerFactory.getLogger(MarkerConfig::class.java)
    val pick = InetAddress.getLocalHost().hostName.equals("taurus", true)
    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    fun scheduledMark(){
        val result =  coinFlip()
        logger.info("Coin Flip Result : $result")
        if (result && markerService.mark().block()){
            logger.info("완료")
        }
    }

    private fun coinFlip() : Boolean{
        val rand = Random(LocalDate.now().toEpochDay())
        val bools = (0..23).map {
            rand.nextBoolean()
        }
        return pick == bools[LocalTime.now().hour]
    }
}
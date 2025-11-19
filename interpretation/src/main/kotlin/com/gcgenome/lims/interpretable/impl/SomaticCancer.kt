package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.dto.InterpretationSomatic
import com.gcgenome.lims.interpretable.Interpretable
import com.gcgenome.lims.test.SomaticCancerPanel
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SomaticCancer(private val om: ObjectMapper): Interpretable {
    override fun chk(sample: Long, service: String): Boolean {
        for (test in com.gcgenome.lims.test.BloodCancerPanel.values()) if (test.code().equals(service, ignoreCase = true)) return true
        // for (test in com.greencross.lims.test.solidtumor.TestInfo.TESTS) if (test.code().equals(service, ignoreCase = true)) return true
        return false
    }
    private fun test(service: String): SomaticCancerPanel {
        for (test in com.gcgenome.lims.test.BloodCancerPanel.values()) if (test.code().equals(service, ignoreCase = true)) return test
        throw RuntimeException()
    }
    override fun interpret(sample: Long, service: String, param: Map<*, *>): Mono<*> {
        if(param.isEmpty()) return negative(sample, service)
        @Suppress("NAME_SHADOWING") val param = om.convertValue(param, InterpretationSomatic::class.java)
        return interpret(sample, service, param)
    }
    fun interpret(sample: Long, service: String, interpretation: InterpretationSomatic): Mono<InterpretationSomatic> {
        val test = test(service)
        interpretation.cancerType = "R/O ${test.referralDefault()}"
        return Mono.just(interpretation)
    }
    override fun negative(sample: Long, service: String): Mono<*> {
        TODO("Not yet implemented")
    }
}
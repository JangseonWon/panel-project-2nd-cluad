package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.dto.InterpretationPanel
import com.gcgenome.lims.dto.ParameterPanel
import com.gcgenome.lims.interpretable.SnvUtil
import com.gcgenome.lims.interpretable.kokr.ClinvarPhraseExplicitKoKr
import com.gcgenome.lims.interpretable.kokr.VariantInterpreterSimpleKoKr
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class Des(om: ObjectMapper, snvRepo: SnvDao) : SnvInterpretable(om, VariantInterpreterSimpleKoKr(clinvarPhrase = ClinvarPhraseExplicitKoKr()), SnvUtil::toSnv, snvRepo) {
    override fun chk(sample: Long, service: String): Boolean {
        if ("ON001".equals(service, ignoreCase = true)) return false
        if ("ON040".equals(service, ignoreCase = true)) return false
        return false
    }
    override fun interpret(sample: Long, service: String, prev: ParameterPanel): Mono<InterpretationPanel> {
        TODO("Not yet implemented")
    }
    override fun negative(sample: Long, service: String): Mono<InterpretationPanel> {
        TODO("Not yet implemented")
    }
}
package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.dto.InterpretationPanel
import com.gcgenome.lims.dto.ParameterPanel
import com.gcgenome.lims.interpretable.SnvUtil
import com.gcgenome.lims.interpretable.kokr.ResultPhraseSimpleKoKr
import com.gcgenome.lims.interpretable.kokr.SummaryPhrase
import com.gcgenome.lims.interpretable.kokr.VariantInterpreterVerboseKoKr
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class Cancer(om: ObjectMapper, snvRepo: SnvDao) : SnvInterpretable(om, VariantInterpreterVerboseKoKr(), SnvUtil::toSnvBrcaCancer, snvRepo) {
    private val summaryInterpreter = SummaryPhrase()
    private val resultInterpreter = ResultPhraseSimpleKoKr()
    override fun chk(sample: Long, service: String): Boolean {
        return false
    }
    override fun interpret(sample: Long, service: String, prev: ParameterPanel): Mono<InterpretationPanel> {
        TODO("Not yet implemented")
    }
    override fun negative(sample: Long, service: String): Mono<InterpretationPanel> {
        TODO("Not yet implemented")
    }
}
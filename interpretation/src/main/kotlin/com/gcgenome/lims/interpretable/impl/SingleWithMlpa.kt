package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.dto.InterpretationPanel
import com.gcgenome.lims.dto.InterpretationPanelMlpa
import com.gcgenome.lims.inserts.Insert
import com.gcgenome.lims.interpretable.Interpretable
import com.gcgenome.lims.interpretable.kokr.MlpaPhraseKoKr
import com.gcgenome.lims.test.SingleGenePanelWithMlpa
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SingleWithMlpa(
    requestDao: RequestDao, snvRepo: SnvDao, inserts: List<Insert>,
    private val om: ObjectMapper
): Interpretable {
    private val single = Single(om, requestDao, snvRepo, inserts, SingleGenePanelWithMlpa.values().map { it.singleGenePanel })
    override fun chk(sample: Long, service: String): Boolean = SingleGenePanelWithMlpa.values().map { it.code() }.contains(service)
    override fun interpret(sample: Long, service: String, param: Map<*, *>): Mono<InterpretationPanelMlpa> {
        val interpretSingle = single.interpret(sample, service, param)
        val mlpa: InterpretationPanelMlpa.Mlpa = (param["previous"] as? Map<*, *>)?.get("mlpa_result")?.let {
            om.convertValue(it, InterpretationPanelMlpa.Mlpa::class.java)
        } ?: InterpretationPanelMlpa.Mlpa(
            result = "Not Detected",
            gene = test(service).singleGenePanel.gene,
            exons = null,
            zygosity = null,
            delDup = null
        )
        return interpretSingle.map {
            val panel = InterpretationPanel (
                result = it.result,
                resultText = it.resultText,
                reasonForReferral = it.reasonForReferral,
                clinicalInformation = it.clinicalInformation,
                meanDepth = it.meanDepth,
                coverage = it.coverage,
                variants = it.variants,
                abbreviationReference = it.abbreviationReference,
                abbreviationDisease = it.abbreviationDisease,
                abbreviation = it.abbreviation,
                interpretation = "${it.interpretation}\n\n${MlpaPhraseKoKr().result(mlpa)}".trimIndent(),
                recommendation = it.recommendation,
                addendum = it.addendum,
                incidentalFindings = it.incidentalFindings,
                authors = it.authors,
                revision = it.revision
            )
            InterpretationPanelMlpa(panel, mlpa)
        }
    }

    override fun negative(sample: Long, service: String): Mono<*> {
        TODO("Not yet implemented")
    }

    fun test(service: String): SingleGenePanelWithMlpa = SingleGenePanelWithMlpa.values().first { service == it.code() }
}
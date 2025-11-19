package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.dto.InterpretationPanel
import com.gcgenome.lims.dto.ParameterPanel
import com.gcgenome.lims.interpretable.SnvUtil
import com.gcgenome.lims.interpretable.kokr.InsilicoPhraseAllKoKr
import com.gcgenome.lims.interpretable.kokr.ResultPhraseSimpleKoKr
import com.gcgenome.lims.interpretable.kokr.SummaryPhrase
import com.gcgenome.lims.interpretable.kokr.VariantInterpreterVerboseKoKr
import com.gcgenome.lims.test.GenePlusPanel
import com.gcgenome.lims.test.HasReferralDefault
import com.gcgenome.lims.test.SingleGenePanel
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Component
class Brca(om: ObjectMapper, private val requestDao: RequestDao, snvRepo: SnvDao) : SnvInterpretable(om, VariantInterpreterVerboseKoKr(insilicoPhrase=InsilicoPhraseAllKoKr()), SnvUtil::toSnvBrcaCancer, snvRepo) {
    private val summaryInterpreter = SummaryPhrase()
    private val resultInterpreter = ResultPhraseSimpleKoKr()
    override fun chk(sample: Long, service: String): Boolean {
        if(GenePlusPanel.S096.code == service || GenePlusPanel.S097.code == service) return true
        return SingleGenePanel.values().stream().filter{ it.gene.contains("BRCA") }.map{ t->t.code() }.anyMatch(service::equals)
    }
    override fun interpret(sample: Long, service: String, param: ParameterPanel): Mono<InterpretationPanel> {
        val test = (GenePlusPanel.values() + SingleGenePanel.values()).stream().filter{ service == it.code() }.findFirst().get()
        val sex = Sex.valueOf(param.sex.name)
        val prev = param.previous
        val diseases = param.disease
        val gene = test.target().substringBefore(' ')       // BRCA1 or BRCA2
        check(gene.contains("BRCA"))
        var mono = interpret(gene, sex, diseases, prev)
        if(prev.addendum!=null) mono = mono.flatMap { builder -> interpret(gene, sex, diseases, prev.addendum).map { builder.apply {
            it.interpretationPrefix = null
            addendum = it.apply {
                val genes = prev.addendum.variants.stream().map { v->v.gene }.distinct().collect(Collectors.joining(", "))
                resultText = "추가로 분석된 $genes 유전자에서 $resultText"
            }.build()
            resultText += " [ADDENDUM RESULT 참조]"
        } } }
        if(prev.incidentalFindings!=null) mono = mono.flatMap { builder -> interpret(gene, sex, diseases, prev.incidentalFindings).map { builder.apply { incidentalFindings = it.build() } } }
        return mono.map { builder ->
                if(param.suffix!=null) builder.appendInterpretation(param.suffix)
                builder.apply {
                    if(test is HasReferralDefault) this.reasonForReferral = "R/O ${test.referralDefault()}"
                    this.clinicalInformation = prev.clinicalInformation
                    this.meanDepth = prev.meanDepth
                    this.coverage = prev.coverage
                    this.recommendation = prev.recommendation
                    this.authors = prev.authors
                    this.revision = prev.revision
                }.build()
            }
    }
    fun interpret(gene: String, sex: Sex, diseases: Map<String, List<Disease>>, prev: InterpretationPanel): Mono<InterpretationPanel.InterpretationPanelBuilder> =
        interpret(gene, sex, prev.variants.map { variant -> variant to diseases[variant.gene].orEmpty() }).map { it.apply { result = summaryInterpreter.summary(variants) } }
    fun interpret(gene: String, sex: Sex, variants: List<Pair<InterpretationPanel.Variant, List<Disease>>>): Mono<InterpretationPanel.InterpretationPanelBuilder> {
        val variantsReportable = variants.stream().map(this::normalize).filter{ reportable() }.collect(Collectors.toList())
        val resultText = resultInterpreter.result(variantsReportable)
        if(variantsReportable.isEmpty()) {
            val interpretation =NEGATIVE_FORMAT.replace("%g", gene)
            return Mono.just(InterpretationPanel.builder().apply {
                this.resultText = resultText
                appendInterpretation(interpretation)
            })
        }
        return InterpretationPanel.builder().apply {
            this.resultText = resultText
        }.appendAll(variantsReportable, sex)
    }
    override fun negative(sample: Long, service: String): Mono<InterpretationPanel> = requestDao.findById(sample, service).flatMap {
        interpret(sample, service, ParameterPanel(ParameterPanel.Sex.valueOf(it.sex), InterpretationPanel.empty(), emptyMap(), ""))
    }
    private fun reportable(): Boolean = true
    companion object {
        private const val NEGATIVE_FORMAT = "%g 유전자의 모든 exon과 인접 intron의 염기서열을 분석한 결과, 질환 관련 변이는 발견되지 않았습니다."
    }
}
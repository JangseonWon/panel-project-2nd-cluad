package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.constants.Clazz
import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.dto.InterpretationPanel
import com.gcgenome.lims.dto.InterpretationPanel.Variant
import com.gcgenome.lims.dto.ParameterPanel
import com.gcgenome.lims.inserts.Insert
import com.gcgenome.lims.interpretable.InterpretationHeaderPhrase
import com.gcgenome.lims.interpretable.SnvUtil
import com.gcgenome.lims.interpretable.kokr.InterpretationHeaderPhraseSimpleKoKr
import com.gcgenome.lims.interpretable.kokr.ResultPhraseSimpleKoKr
import com.gcgenome.lims.interpretable.kokr.SummaryPhrase
import com.gcgenome.lims.interpretable.kokr.VariantInterpreterSimpleKoKr
import com.gcgenome.lims.test.HasCode
import com.gcgenome.lims.test.HasName
import com.gcgenome.lims.test.HasReferralDefault
import reactor.core.publisher.Mono
import java.util.stream.Collectors

abstract class AbstractPanel<T>(
    om: ObjectMapper,
    private val requestDao: RequestDao,
    snvRepo: SnvDao,
    private val inserts: List<Insert>,
    private val interpretationHeaderInterpreter: InterpretationHeaderPhrase = InterpretationHeaderPhraseSimpleKoKr(),
)  : SnvInterpretable(om, VariantInterpreterSimpleKoKr(), SnvUtil::toSnv, snvRepo) where T: HasCode, T: HasName {
    private val summaryInterpreter = SummaryPhrase()
    private val resultInterpreter = ResultPhraseSimpleKoKr()
    abstract fun test(service: String): T
    abstract fun referralDefault(service: String): HasReferralDefault?
    override fun interpret(sample: Long, service: String, param: ParameterPanel): Mono<InterpretationPanel> {
        val test = test(service)
        val sex = Sex.valueOf(param.sex.name)
        val prev = param.previous
        val diseases = param.disease
        val reasonForReferral = referralDefault(service)?.let { "R/O ${it.referralDefault()}" }

        var mono = interpret(test, test, sex, diseases, prev)
        if(prev.addendum!=null) mono = mono.flatMap { builder -> interpret(test, test, sex, diseases, prev.addendum).map { builder.apply {
            it.interpretationPrefix = null
            addendum = it.apply {
                val genes = prev.addendum.variants.stream().map { v->v.gene }.distinct().collect(Collectors.joining(", "))
                resultText = "추가로 분석된 $genes 유전자에서 $resultText"
            }.build()
            resultText += " [ADDENDUM RESULT 참조]"
        } } }
        if(prev.incidentalFindings!=null) mono = mono.flatMap { builder -> interpret(test, test, sex, diseases, prev.incidentalFindings).map { builder.apply { incidentalFindings = it.build() } } }
        return mono.map { builder ->
            val pre = getInserts(service, inserts, Insert.Position.PRESUFFIX).firstOrNull()
            val post = getInserts(service, inserts, Insert.Position.POSTSUFFIX).firstOrNull()
            if(pre != null) builder.appendInterpretation(pre)
            if(param.suffix!=null) builder.appendInterpretation(param.suffix)
            if(post != null) builder.appendInterpretation(post)
            builder.apply {
                this.reasonForReferral = reasonForReferral
                this.clinicalInformation = prev.clinicalInformation
                this.meanDepth = prev.meanDepth
                this.coverage = prev.coverage
                this.recommendation = prev.recommendation
                this.authors = prev.authors
                this.revision = prev.revision
            }.build()
        }
    }
    fun interpret(hasCode: HasCode, hasName: HasName, sex: Sex, diseases: Map<String, List<Disease>>, prev: InterpretationPanel): Mono<InterpretationPanel.InterpretationPanelBuilder> =
        interpret(hasCode, hasName, sex, prev.variants.map { variant -> variant to diseases[variant.gene].orEmpty() }).map { it.apply { result = summaryInterpreter.summary(variants) } }
    fun interpret(hasCode: HasCode, hasName: HasName, sex: Sex, variants: List<Pair<Variant, List<Disease>>>): Mono<InterpretationPanel.InterpretationPanelBuilder> {
        val variantsReportable = variants.stream().map(this::normalize).filter{ reportable(it.first.clazz) }.collect(Collectors.toList())
        val resultText = resultInterpreter.result(variantsReportable)
        if(variantsReportable.isEmpty()) {
            val interpretation = getInserts(hasCode.code(), inserts, Insert.Position.NEGATIVE, hasName.name()).first()
            return Mono.just(InterpretationPanel.builder().apply {
                this.resultText = resultText
                appendInterpretation(interpretation)
            })
        }
        return InterpretationPanel.builder().apply {
            this.resultText = resultText
            this.interpretationPrefix = interpretationHeaderInterpreter.result(variantsReportable).replace(
                "%p",
                if (hasName.name().contains("gene")) "${hasName.name().split("gene")[0]}유전자"
                else hasName.name().replace("검사", "")
            )
        }.appendAll(variantsReportable, sex)
    }
    override fun negative(sample: Long, service: String): Mono<InterpretationPanel> = requestDao.findById(sample, service).flatMap {
        interpret(sample, service, ParameterPanel(ParameterPanel.Sex.valueOf(it.sex), InterpretationPanel.empty(), emptyMap(), ""))
    }
    open fun reportable(clazz: Clazz): Boolean = when(clazz) {
        Clazz.PV        -> true
        Clazz.LPV       -> true
        Clazz.VUS       -> true
        else        -> false
    }
}
package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.dto.InterpretationPanel
import com.gcgenome.lims.dto.ParameterPanel
import com.gcgenome.lims.inserts.Insert
import com.gcgenome.lims.interpretable.*
import com.gcgenome.lims.interpretable.variant.Snv
import org.springframework.data.elasticsearch.core.document.Document
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function
import java.util.stream.Collectors

abstract class SnvInterpretable(
    private val om: ObjectMapper,
    private val interpreter: VariantInterpreter<Snv>,
    private val toSnv: Function<Triple<InterpretationPanel.Variant, List<Disease>, Document?>, Snv>,
    private val snvRepo: SnvDao
): Interpretable, DocumentUtil {
    override fun interpret(sample: Long, service: String, param:Map<*, *>): Mono<InterpretationPanel> {
        @Suppress("NAME_SHADOWING") val param = om.convertValue(param, ParameterPanel::class.java)
        return interpret(sample, service, param)
    }
    abstract fun interpret(sample: Long, service: String, prev: ParameterPanel): Mono<InterpretationPanel>
    abstract override fun negative(sample: Long, service: String): Mono<InterpretationPanel>
    protected fun normalize(param: Pair<InterpretationPanel.Variant, List<Disease>>): Pair<InterpretationPanel.Variant, List<Disease>> = normalize(param.first, param.second)
    protected fun normalize(variant: InterpretationPanel.Variant, diseases: List<Disease>): Pair<InterpretationPanel.Variant, List<Disease>> = Pair(
        InterpretationPanel.Variant(
            variant.snv, variant.analysis,
            variant.gene,
            HgvscUtil.trimOrDefault(variant.hgvsc), HgvspUtil.trimOrDefault(variant.hgvsp),
            HgvscUtil.trimOrDefault(variant.originHgvsc), HgvspUtil.trimOrDefault(variant.originHgvsp),
            variant.zygosity,
            DiseaseUtil.abbreviationShort(diseases),
            diseases.stream().map { d -> d.fullName }.collect(Collectors.joining(";")),
            InheritanceUtil.abbreviationShort(diseases),
            variant.interpretation, variant.genPhenDb,
            variant.clazz),
        diseases)
    private fun appendDocument(param: Pair<InterpretationPanel.Variant, List<Disease>>): Mono<Triple<InterpretationPanel.Variant, List<Disease>, Document?>> {
        val (variant, diseases) = param
        val snv = variant.snv
        val analysis = variant.analysis
        if(snv == null || analysis == null) return Mono.empty()
        val batch = analysis.trim().split(":".toRegex()).toTypedArray()[1]
        return snvRepo.find(batch, "$analysis:$snv")
            .map { doc -> Triple(variant, diseases, doc) }
            .switchIfEmpty(Mono.just(Triple(variant, diseases, null)))
    }
    protected fun InterpretationPanel.InterpretationPanelBuilder.appendAll(variants: List<Pair<InterpretationPanel.Variant, List<Disease>>>, sex: Sex): Mono<InterpretationPanel.InterpretationPanelBuilder> {
        interpreter.initialize()
        return Flux.fromIterable(variants).flatMapSequential(::appendDocument)
            .map { (variant, diseases, document) ->
                val chr = document?.getString("chrom")
                val zygosity = chr?.let { ZygosityUtil.normalize(variant.zygosity, sex, it) }
                appendReference(ReferenceUtil.abbreviation(variant))
                    .appendDisease(DiseaseUtil.abbreviation(diseases))
                    .appendInheritance(InheritanceUtil.abbreviation(diseases))
                    .appendClass(variant.clazz.abbreviation())
                if (zygosity != null) appendZygosity(ZygosityUtil.abbreviation(zygosity))
                @Suppress("NAME_SHADOWING") val variant = InterpretationPanel.Variant(
                    variant.snv, variant.analysis,
                    variant.gene,
                    HgvscUtil.extractCodingPart(variant.hgvsc), variant.hgvsp,
                    variant.originHgvsc, variant.originHgvsp,
                    if (zygosity != null) ZygosityUtil.toShort(zygosity) else variant.zygosity,
                    variant.disease,
                    variant.diseaseFullName,
                    variant.inheritance,
                    variant.interpretation, variant.genPhenDb,
                    variant.clazz
                )
                appendVariant(variant)
                val snv = toSnv.apply(Triple(variant, diseases, document))
                appendInterpretation(interpreter.interpret(snv))
            }.then(Mono.just(this))
    }

    protected fun getInserts(service : String, inserts : List<Insert>, position: Insert.Position, textInput : Any? = null) : List<String> {
        return inserts.filter { it.position() == position && it.chk{insert -> insert.services().contains(service)} }.sortedBy { it.weight() }.map { it.text(textInput) }
    }

    protected fun getInserts(inserts : List<Insert>, position: Insert.Position, textInput : Any? = null, predicateLambda : (Insert) -> Boolean): List<String> {
        return inserts.filter { it.position() == position && it.chk(predicateLambda) }.sortedBy { it.weight() }.map { it.text(textInput) }
    }
}
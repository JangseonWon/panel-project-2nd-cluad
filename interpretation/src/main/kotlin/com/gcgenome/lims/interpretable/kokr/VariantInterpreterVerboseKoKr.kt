package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.interpretable.*
import com.gcgenome.lims.interpretable.variant.Snv
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Collectors

internal class VariantInterpreterVerboseKoKr(
    private val clazzPhrase: VariantClassPhrase = VariantClassPhraseVerbose(),
    private val populationPhrase: PopulationPhrase = PopulationPhraseKoKr(),
    private val clinvarPhrase: ClinvarPhrase = ClinvarPhraseVerboseKoKr(),
    private val insilicoPhrase: InsilicoPhrase = InsilicoPhraseKoKr()
): VariantInterpreter<Snv> {
    private val hasMaf: AtomicBoolean = AtomicBoolean(false)
    override fun initialize() = hasMaf.set(false)
    override fun interpret(variant: Snv): String {
        val header      = "${variant.gene()}에서 "
        val hgvsp       = if(variant.hgvsp()!=null) "${HgvspPhraseKoKr.explainHgvsp(variant.hgvsp()!!)}될 것으로 예상 되는 " else null
        val hgvsc       = "${HgvscPhraseKoKr.explainHgvsc(variant.hgvsc())}${if (hgvsp != null) "되어 " else "되는 "}"
        val clazz       = clazzPhrase.normalize(variant.clazz()) + "인 " + variant.hgvsc() + "가 발견되었습니다."
        val paragraph1  = listOf(header, hgvsc, hgvsp, clazz).stream().filter(Objects::nonNull).map{it!!.trim()}.filter(String::isNotEmpty).collect(Collectors.joining(" "))
        val clinvar     = clinvarPhrase.clinvar(variant)
        val pop         = "${populationPhrase.population(variant, hasMaf.getAndSet(true))}${if (clinvar == null) "변이입니다." else "변이로,"}"
        val paragraph2  = listOf(pop, clinvar).stream().filter(Objects::nonNull).map{it!!.trim()}.filter(String::isNotEmpty).collect(Collectors.joining(" ")).let {
            if(it.isNotEmpty()) " 이 변이는 $it"
            else it
        }
        val insilico    = insilicoPhrase.insilico(variant)
        val paragraph3  = listOf(insilico).stream().filter(Objects::nonNull).map{it!!.trim()}.filter(String::isNotEmpty).collect(Collectors.joining(" ")).let {
            if(it.isNotEmpty()) it.replaceFirstChar(Char::uppercase).replace("PolyPhen,", "PolyPhen-2,")
            else it
        }
        return listOf(paragraph1, paragraph2, paragraph3).stream().filter(Objects::nonNull).map(String::trim).filter(String::isNotEmpty).collect(Collectors.joining(" "))
    }
}
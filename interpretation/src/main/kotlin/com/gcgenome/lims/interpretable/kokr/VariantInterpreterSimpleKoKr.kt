package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.interpretable.ClinvarPhrase
import com.gcgenome.lims.interpretable.InsilicoPhrase
import com.gcgenome.lims.interpretable.PopulationPhrase
import com.gcgenome.lims.interpretable.VariantInterpreter
import com.gcgenome.lims.interpretable.variant.Disease
import com.gcgenome.lims.interpretable.variant.Snv
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Collectors

class VariantInterpreterSimpleKoKr(
    private val populationPhrase: PopulationPhrase = PopulationPhraseKoKr(),
    private val clinvarPhrase: ClinvarPhrase = ClinvarPhraseSimpleKoKr(),
    private val insilicoPhrase: InsilicoPhrase = InsilicoPhraseKoKr()
): VariantInterpreter<Snv> {
    private val hasMaf: AtomicBoolean = AtomicBoolean(false)
    override fun initialize() = hasMaf.set(false)
    override fun interpret(variant: Snv): String {
        val paragraph1  = if(variant.interpretation()==null) {
            val hgvs        = hgvs(variant)
            val insilico    = insilicoPhrase.insilico(variant)
            val pop         = "${populationPhrase.population(variant, hasMaf.getAndSet(true))}${if (insilico == null) "변이입니다." else "변이로,"}"
            val hgmd        = hgmd(variant)
            val vus         = if(variant.clazz() == "VUS") "질환 관련성에 대한 근거가 불충분하여 VUS로 분류됩니다" else null
            val clinvar     = clinvarPhrase.clinvar(variant)
            val hgmdClinvar =
                if(clinvar!=null && hgmd!=null && vus!=null)  "이 변이는 ${hgmd.replace("있습니다(", "있으나(")}, ${vus.replace("분류됩니다", "분류되며")}, $clinvar."
                else if(clinvar!=null && hgmd!=null)          "이 변이는 ${hgmd.replace("있습니다(", "있으며(")}, $clinvar."  // 질환이 있는 사례가 발견되었고, clinvar에서는..
                else if(clinvar!=null && vus!=null)           "이 변이는 ${vus.replace("분류됩니다", "분류되며")}, $clinvar."  // 아마도 clinvar 클래스도 VUS일 가능성이 높음
                else if(hgmd!=null && vus!=null)              "이 변이는 ${hgmd.replace("있습니다(", "있으나(")}, $vus."      // 질환이 있는 사례가 발견되었으나, 근거가 불충분하여...
                else if(clinvar!=null)                        "이 변이는 $clinvar."
                else if(hgmd!=null)                           "이 변이는 $hgmd."
                else if(vus!=null)                            "이 변이는 $vus."
                else null
            listOf(hgvs, pop, insilico, hgmdClinvar).stream().filter(Objects::nonNull).map{it!!.trim()}.filter(String::isNotEmpty).collect(Collectors.joining(" "))
        } else variant.interpretation()!!
        val disease     = disease(variant)
        val paragraph2  = listOf(disease).stream().filter(Objects::nonNull).map{it!!.trim()}.filter(String::isNotEmpty).collect(Collectors.joining(" "))
        return listOf(paragraph1, paragraph2).stream().filter(Objects::nonNull).map(String::trim).filter(String::isNotEmpty).collect(Collectors.joining("\n\n"))
    }
    private fun hgvs(variant: Snv): String {
        val gene = variant.gene()
        val hgvsc = variant.hgvsc()
        val hgvsp = variant.hgvsp()
        return if(hgvsp!=null) "$gene 유전자의 $hgvsc ($hgvsp) 변이는"
        else "$gene 유전자의 $hgvsc 변이는"
    }
    private fun hgmd(variant: Snv): String? {
        if(variant.hgmdPmid()==null) return null
        val disease = variant.gene() + "-related disease 환자에서 "
        return "기존에 ${disease}보고된 바 있습니다(PMID: ${variant.hgmdPmid()})"
    }
    private fun disease(variant: Snv): String? {
        if(variant.disease().isEmpty()) return null
        val diseases = variant.disease().stream().map(Disease::shortName).collect(Collectors.joining(", "))
        val inheritances = variant.disease().stream().map(Disease::inheritance).map { it.split("," ) }
            .flatMap(List<String>::stream).map(String::trim).distinct().collect(Collectors.joining(" 또는 "))
        return "${variant.gene()} 유전자는 $diseases 관련 유전자로 ${if (inheritances.isEmpty()) "알려져 있습니다." else "$inheritances 유전 양상을 보입니다."}"
    }
}
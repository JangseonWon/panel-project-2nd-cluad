package com.gcgenome.test

import com.gcgenome.lims.constants.Clazz
import com.gcgenome.lims.interpretable.kokr.VariantInterpreterSimpleKoKr
import com.gcgenome.lims.interpretable.variant.DiseaseImpl
import com.gcgenome.lims.interpretable.variant.SnvImpl
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.Ignore
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
@Ignore
internal class VariantInterpreterSimpleKoKrTest {
    private val interpreter =  VariantInterpreterSimpleKoKr()
    @Test
    @DisplayName("20220513-171-5228 GRN 유전자 VUS 변이")
    internal fun test1() {
        val variant = SnvImpl("GRN",
            "c.662G>C", "p.Cys221Ser",
            "Het", listOf(
                DiseaseImpl("Aphasia, primary progressive", "FTD", "AD")
            ), Clazz.VUS,
            "0.0088",  "0.3636",
            "deleterious", "probably_damaging", "D&D",
            "935555", "Uncertain significance",
            "Frontotemporal_dementia", "20020531")
        interpreter.initialize()
        val success = "GRN 유전자의 c.662G>C (p.Cys221Ser) 변이는 전체 인구 집단(gnomAD)에서의 minor allele frequency (MAF)가 0.0088%이며 " +
                "한국인 인구 집단(KRGDB)에서는 0.3636%인 변이로, in-silico prediction(SIFT, PolyPhen, MutationTaster)에서는 Deleterious로 예측되었습니다.\r\n" +
                "이 변이는 기존에 Frontotemporal dementia 환자에서 보고된 바 있으며(PMID: 20020531), ClinVar에서 Uncertain significance로 분류되어 있습니다(ID: 935555). " +
                "GRN 유전자는 FTD 관련 유전자로 AD 유전 양상을 보입니다."
        assertEquals(success,interpreter.interpret(variant))
    }
    @Test
    @DisplayName("20220506-171-5032 LDLR 유전자 LPV 변이")
    internal fun test2() {
        val variant = SnvImpl("LDLR",
            "c.1592_1627del", "p.Met531_Ile542del",
            "Het", listOf(
                DiseaseImpl("Familial hypercholesterolemia 1", "FH1", "AD, AR")
            ), Clazz.LPV,
            null,null,
            null, null, null,
            "251924", "Likely_pathogenic")
        interpreter.initialize()
        val success = "LDLR 유전자의 c.1592_1627del (p.Met531_Ile542del) 변이는 일반 인구 집단(gnomAD, KRGDB)에서 보고된 바 없는 " +
                "매우 드문 변이입니다.\r\n" +
                "이 변이는 ClinVar에서 LPV로 분류되어 있습니다(ID: 251924). LDLR 유전자는 FH1 관련 유전자로 AD 또는 AR 유전 양상을 보입니다."
        assertEquals(interpreter.interpret(variant), success)
    }
    @Test
    @DisplayName("20220506-171-5032 PCSK9 유전자 LPV 변이")
    internal fun test3() {
        val variant = SnvImpl("PCSK9",
            "c.94G>A", "p.Glu32Lys",
            "Het", listOf(
                DiseaseImpl("Familial hypercholesterolemia 3", "FH3", "AD")
            ), Clazz.LPV,
            "0.0028",  null,
            "deleterious_low_confidence", "benign", "N&N",
            "297692", "Conflicting_interpretations_of_pathogenicity|Pathogenic(3)&_Likely_pathogenic(1)&_Uncertain_significance(2)",
            "High_LDL_cholesterol", "17316651")
        interpreter.initialize()
        val success = "PCSK9 유전자의 c.94G>A (p.Glu32Lys) 변이는 전체 인구 집단(gnomAD)에서의 minor allele frequency (MAF)가 " +
                "0.0028%이며 한국인 인구 집단(KRGDB)에서는 보고된 바 없는 변이로, in-silico prediction에서는 서로 다르게 " +
                "예측되었습니다(SIFT: Deleterious, PolyPhen, MutationTaster: Tolerated).\r\n" +
                "이 변이는 기존에 High LDL cholesterol 환자에서 보고된 바 있으며(PMID: 17316651), ClinVar에서 PV(3) 또는 LPV(1) 또는 VUS(2)로 분류되어 있습니다(ID: 297692). " +
                "PCSK9 유전자는 FH3 관련 유전자로 AD 유전 양상을 보입니다."
        assertEquals(interpreter.interpret(variant), success)
    }
    @Test
    @DisplayName("20220503-171-5276 CALM1 유전자 VUS 변이")
    internal fun test4() {
        val variant = SnvImpl("CALM1",
            "c.167T>C", "p.Val56Ala",
            "Het", listOf(
                DiseaseImpl("Catecholaminergic polymorphic ventricular tachycardia 4", "CPVT4", "AD"),
                DiseaseImpl("Long QT syndrome 14", "LQTS14", "AD")
            ), Clazz.VUS,
            null,  null,
            "deleterious_low_confidence", "benign", "D&D&D&D")
        interpreter.initialize()
        val success = "CALM1 유전자의 c.167T>C (p.Val56Ala) 변이는 일반 인구 집단(gnomAD, KRGDB)에서 보고된 바 없는 매우 드문 변이로, " +
                "in-silico prediction에서는 서로 다르게 예측되었습니다(SIFT, MutationTaster: Deleterious, PolyPhen: Tolerated).\r\n" +
                "CALM1 유전자는 CPVT4, LQTS14 관련 유전자로 AD 유전 양상을 보입니다."
        assertEquals(interpreter.interpret(variant), success)
    }
    @Test
    @DisplayName("20210415-171-5128 CACNA1C 유전자 PV 변이")
    internal fun test5() {
        val variant = SnvImpl("CACNA1C",
            "c.1114-316G>A", "p.(?)",
            "Het", listOf(
                DiseaseImpl("Timothy syndrome", "TS", "AD")
            ), Clazz.PV,
            null,  null,
            null, null, null,
            "155775", "Pathogenic")
        interpreter.initialize()
        val success = "CACNA1C 유전자의 c.1114-316G>A 변이는 일반 인구 집단(gnomAD, KRGDB)에서 보고된 바 없는 매우 드문 변이입니다.\r\n" +
                "이 변이는 ClinVar에서 PV로 분류되어 있습니다(ID: 155775). CACNA1C 유전자는 TS 관련 유전자로 AD 유전 양상을 보입니다."
        assertEquals(interpreter.interpret(variant), success)
    }
}
package com.gcgenome.test

import com.gcgenome.lims.constants.Clazz
import com.gcgenome.lims.interpretable.kokr.VariantInterpreterVerboseKoKr
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
internal class VariantInterpreterVerboseKoKrTest {
    private val interpreter =  VariantInterpreterVerboseKoKr()
    @Test
    @DisplayName("20220527-171-5222 BRCA2 유전자 VUS 변이")
    internal fun test1() {
        val variant = SnvImpl("BRCA2",
            "c.623T>G", "p.Val208Gly",
            "Het", listOf(
                DiseaseImpl("Hereditary breast and ovarian cancer", "HBOC", "AD")
            ), Clazz.VUS,
            "0.0041",  "0.1364",
            "D", "D", "D",
            "52040", "Uncertain_significance",
            null, null)
        interpreter.initialize()
        val success = "BRCA2에서 623번째 위치한 염기서열 T가 G로 치환되어 208번째 아미노산인 Valine이 Glycine으로 치환될 것으로 예상 되는 " +
                "Variant of Uncertain Significance인 c.623T>G가 발견되었습니다.\r\n이 변이는 전체 인구 집단(gnomAD)에서의 minor allele frequency (MAF)가 0.0041%이며 " +
                "한국인 인구 집단(KRGDB)에서는 0.1364%인 변이로, Clinvar에서 Uncertain significance로 분류되어 있습니다(ID: 52040).\r\nIn-silico prediction(SIFT, PolyPhen-2, " +
                "MutationTaster)에서는 Deleterious로 예측되었습니다."
        assertEquals(interpreter.interpret(variant), success)
    }
    @Test
    @DisplayName("20220516-171-5275 BRCA2 유전자 PV 변이")
    internal fun test2() {
        val variant = SnvImpl("BRCA2",
            "c.994del", "p.Ile332PhefsTer17",
            "Het", listOf(
                DiseaseImpl("Hereditary breast and ovarian cancer", "HBOC", "AD")
            ), Clazz.PV,
            null,null,
            null, null, null,
            "52924", "Pathogenic")
        interpreter.initialize()
        val success = "BRCA2에서 994번째 위치한 염기가 삭제되어 332번째 아미노산인 Isoleucine이 Phenylalanine으로 치환되고 해석 틀이 변하여 다음 17번째 아미노산이 " +
                "stop codon으로 변경될 것으로 예상 되는 Pathogenic Variant인 c.994del가 발견되었습니다.\r\n이 변이는 일반 인구 집단(gnomAD, KRGDB)에서 보고된 바 없는 매우 드문 " +
                "변이로, Clinvar에서 Pathogenic으로 분류되어 있습니다(ID: 52924)."
        assertEquals(interpreter.interpret(variant), success)
    }
}
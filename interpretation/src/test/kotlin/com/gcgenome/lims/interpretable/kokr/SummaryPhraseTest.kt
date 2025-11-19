package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.constants.Clazz
import com.gcgenome.lims.dto.InterpretationPanel
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class SummaryPhraseTest{
    private val summaryPhrase = SummaryPhrase()

    @Test
    @DisplayName("Result 텍스트 생성, inheritance 반영")
    internal fun testSummary1() {
        val var1 = createVariantMock(gene = "TEST1", clazz = Clazz.LBV, inheritance = summaryPhrase.dominants.random())
        val var2 = createVariantMock(gene = "TEST2", clazz = Clazz.BV, inheritance = summaryPhrase.recessives.random())
        val var3 = createVariantMock(gene = "TEST3", clazz = Clazz.VUS, inheritance =  summaryPhrase.dominants.random())
        val var4 = createVariantMock(gene = "TEST4", clazz = Clazz.PV, inheritance = summaryPhrase.recessives.random())
        val var5 = createVariantMock(gene = "TEST5", clazz = Clazz.LPV, inheritance =  summaryPhrase.dominants.random())
        val var6 = createVariantMock(gene = "TEST6", clazz = Clazz.PV, inheritance = summaryPhrase.dominants.random())
        val var7 = createVariantMock(gene = "TEST7", clazz = Clazz.PV, inheritance = "AD, AR")
        val list : MutableList<InterpretationPanel.Variant> = mutableListOf(var1, var2)
        assertEquals("NEGATIVE",summaryPhrase.summary(list))
        list.addAll(listOf(var3, var4))
        assertEquals("INCONCLUSIVE",summaryPhrase.summary(list))
        list.addAll(listOf(var5, var6))
        assertEquals("POSITIVE",summaryPhrase.summary(list))
        assertEquals("INCONCLUSIVE",summaryPhrase.summary(mutableListOf(var7)))
    }

    @Test
    @DisplayName("Result 텍스트 생성, gene 중복 확인")
    internal fun testSummary2() {
        val var1 = createVariantMock(gene = "TEST1", clazz = Clazz.PV, inheritance =  summaryPhrase.recessives.random())
        val var2 = createVariantMock(gene = "TEST2", clazz = Clazz.PV, inheritance = summaryPhrase.recessives.random())
        val var3 = createVariantMock(gene = "TEST3", clazz = Clazz.PV, inheritance = summaryPhrase.recessives.random())
        val var4 = createVariantMock(gene = "TEST3", clazz = Clazz.PV, inheritance = summaryPhrase.recessives.random())
        val var5 = createVariantMock(gene = "TEST4", clazz = Clazz.LPV, inheritance = summaryPhrase.recessives.random())
        val var6 = createVariantMock(gene = "TEST4", clazz = Clazz.LPV, inheritance = summaryPhrase.recessives.random())
        assertEquals("INCONCLUSIVE",summaryPhrase.summary(listOf(var1, var2)))
        assertEquals("POSITIVE",summaryPhrase.summary(listOf(var3, var4)))
        assertEquals("POSITIVE",summaryPhrase.summary(listOf(var5, var6)))
    }

    @Test
    @DisplayName("Result 텍스트 생성, zygosity 확인")
    internal fun testSummary3() {
        val var1 = createVariantMock(gene = "TEST1", clazz = Clazz.PV, inheritance = summaryPhrase.recessives.random(), zygosity = "Het")
        val var2 = createVariantMock(gene = "TEST2", clazz = Clazz.PV, inheritance = summaryPhrase.recessives.random(), zygosity = "")
        val var3 = createVariantMock(gene = "TEST3", clazz = Clazz.PV, inheritance = summaryPhrase.recessives.random(), zygosity = "Hom")
        val var4 = createVariantMock(gene = "TEST4", clazz = Clazz.PV, inheritance = summaryPhrase.recessives.random(), zygosity = "Hem")
        val var5 = createVariantMock(gene = "TEST5", clazz = Clazz.LPV, inheritance = summaryPhrase.recessives.random(), zygosity = "Hem")
        val var6 = createVariantMock(gene = "TEST6", clazz = Clazz.LPV, inheritance = summaryPhrase.recessives.random(), zygosity = "Hom")
        println(var1.inheritance)
        println(var2.inheritance)
        assertEquals("INCONCLUSIVE",summaryPhrase.summary(listOf(var1, var2)))
        assertEquals("POSITIVE",summaryPhrase.summary(listOf(var3, var4)))
        assertEquals("POSITIVE",summaryPhrase.summary(listOf(var5, var6)))
        assertEquals("POSITIVE",summaryPhrase.summary(listOf(var1, var4)))
    }

    private fun createVariantMock(gene : String = "", clazz : Clazz, inheritance : String, zygosity : String = "") : InterpretationPanel.Variant{
        val mock = mockk<InterpretationPanel.Variant>()
        every { mock.gene } returns(gene)
        every { mock.clazz } returns(clazz)
        every { mock.inheritance } returns(inheritance)
        every { mock.zygosity } returns(zygosity)
        return mock
    }
}

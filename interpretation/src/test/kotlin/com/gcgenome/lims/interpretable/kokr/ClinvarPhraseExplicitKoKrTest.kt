package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.interpretable.variant.Snv
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ClinvarPhraseExplicitKoKrTest{
    private val mapper =  ClinvarPhraseExplicitKoKr()
    private val testValues = listOf("Benign/Likely_benign", "Benign", "Likely_benign", "Conflicting_interpretations_of_pathogenicity|Uncertain_significance(1)&_Benign(5)&_Likely_benign(1)", "Conflicting_interpretations_of_pathogenicity|Uncertain_significance(1)&_Benign(8)", "Conflicting_interpretations_of_pathogenicity|Uncertain_significance(5)&_Benign(5)&_Likely_benign(1)", "Conflicting_interpretations_of_pathogenicity|Uncertain_significance(1)&_Benign(1)")

    @Test
    @DisplayName("Clinvar 명시 파싱 체크")
    internal fun testValues() {
        val results = testValues.map(::stringToSnvMock).map(mapper::clinvar).listIterator()
        assertEquals("ClinVar에서 Benign/Likely Benign로 분류되어 있습니다(ID: MockID)",results.next())
        assertEquals("ClinVar에서 Benign로 분류되어 있습니다(ID: MockID)",results.next())
        assertEquals("ClinVar에서 Likely Benign로 분류되어 있습니다(ID: MockID)",results.next())
        assertEquals("ClinVar에서 Uncertain significance(1) 또는 Benign(5) 또는 Likely Benign(1)로 분류되어 있습니다(ID: MockID)",results.next())
        assertEquals("ClinVar에서 Uncertain significance(1) 또는 Benign(8)로 분류되어 있습니다(ID: MockID)",results.next())
        assertEquals("ClinVar에서 Uncertain significance(5) 또는 Benign(5) 또는 Likely Benign(1)로 분류되어 있습니다(ID: MockID)",results.next())
        assertEquals("ClinVar에서 Uncertain significance(1) 또는 Benign(1)로 분류되어 있습니다(ID: MockID)",results.next())
    }

    private fun stringToSnvMock(clazz : String) : Snv {
        val newMock = mockk<Snv>()
        every { newMock.clinvarClass() }.returns(clazz)
        every { newMock.clinvarId() }.returns("MockID")
        return newMock
    }
}
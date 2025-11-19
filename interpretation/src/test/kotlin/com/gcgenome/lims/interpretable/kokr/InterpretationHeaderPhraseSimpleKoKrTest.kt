package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.constants.Clazz
import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.dto.InterpretationPanel
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


internal class InterpretationHeaderPhraseSimpleKoKrTest {
    val var1 = InterpretationPanel.Variant("", "", "Gene1", "", "", "","","","",Clazz.VUS)
    val var2 = InterpretationPanel.Variant("", "", "Gene2", "", "", "","","","",Clazz.LBV)
    val var3 = InterpretationPanel.Variant("", "", "Gene3", "", "", "","","","",Clazz.PV)
    val var4 = InterpretationPanel.Variant("", "", "Gene2", "", "", "","","","",Clazz.LBV)
    val var5 = InterpretationPanel.Variant("", "", "Gene2", "", "", "","","","",Clazz.LBV)
    val diseaseMock = mockk<Disease>()
    @Test
    @DisplayName("유전자 클래스에 따라 묶음")
    fun testSummary(){
        val headerInterpreter = InterpretationHeaderPhraseSimpleKoKr()
        val header = headerInterpreter.result(listOf(var1 to listOf(diseaseMock), var2 to listOf(diseaseMock), var3 to listOf(diseaseMock), var4 to listOf(diseaseMock), var5 to listOf(diseaseMock)))
        assertEquals("%p 분석 결과, Gene3 유전자에서 PV가 발견되었고, Gene1 유전자에서 VUS가 발견되었고, Gene2 유전자에서 LBV가 발견되었습니다.", header)
    }
}
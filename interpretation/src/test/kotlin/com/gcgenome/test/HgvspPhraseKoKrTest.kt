package com.gcgenome.test

import com.gcgenome.lims.interpretable.kokr.HgvspPhraseKoKr
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals

@ExtendWith(SpringExtension::class)
internal class HgvspPhraseKoKrTest {
    @Test @DisplayName("Nonsense")
    internal fun nonsense() {
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Trp636*"), "636번째 아미노산인 Tryptophan이 stop codon으로 치환")
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Arg2494Ter"), "2494번째 아미노산인 Arginine이 stop codon으로 치환")
    }
    @Test @DisplayName("Missense")
    internal fun missense() {
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Arg67His"), "67번째 아미노산인 Arginine이 Histidine으로 치환")
    }
    @Test @DisplayName("StopRetain")
    internal fun stopRetain() {
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Ter636="), "636번 코돈인 종결 코돈이 그대로 보전")
    }
    @Test @DisplayName("StopLoss")
    internal fun stopLoss() {
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Ter636Trpext*?"), "636번 코돈인 종결코돈이 Tryptophan로 치환되면서 단백질 길이가 연장")
    }
    @Test @DisplayName("Inframe")
    internal fun inframe() {
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Val1578del"), "1578번째 아미노산인 Valine이 제거 되면서 단백질 길이가 축소")
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Val125dup"), "125번째 아미노산인 Valine이 중복")
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Tyr469_Asn471del"), "469번째 아미노산인 Tyrosine으로부터 471번째 아미노산인 Asparagine까지 제거 되면서 단백질 길이가 축소")
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Asn771_His773dup"), "771번째 아미노산인 Asparagine으로부터 773번째 아미노산인 Histidine까지 한번 더 중복")
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Thr93_Leu94delinsIle"), "93번째 아미노산인 Threonine으로부터 94번째 아미노산인 Leucine까지 제거되고 그 위치에 Isoleucine이 삽입")
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Glu91_Cys92insAsnGlu"), "91번째 아미노산인 Glutamic acid로부터 92번째 아미노산인 Cysteine사이에 Asparagine, Glutamic acid가 삽입")
    }
    @Test @DisplayName("StartLost")
    internal fun startLost() {
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Met1?"), "첫번째 아미노산인 Methionine의 시작코돈이 변경")
    }
    @Test @DisplayName("Synonymus")
    internal fun synonymus() {
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Glu2904="), "2904번째 아미노산인 Glutamic acid가 그대로 보존")
    }
    @Test @DisplayName("Frameshift")
    internal fun frameshift() {
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Glu1210ArgfsTer9"), "1210번째 아미노산인 Glutamic acid가 Arginine으로 치환되고 해석 틀이 변하여 다음 9번째 아미노산이 stop codon으로 변경")
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Ter1361IlefsTer4"), "1361번째 Stop codon이 Isoleucine으로 치환되고 해석 틀이 변하여 다음 4번째 아미노산이 stop codon으로 변경")
        assertEquals(HgvspPhraseKoKr.explainHgvsp("p.Glu1069Argfs"), "1069번째 아미노산인 Glutamic acid가 Arginine으로 치환되고 해석 틀이 변하여 다음 아미노산이 stop codon으로 변경")
    }
}
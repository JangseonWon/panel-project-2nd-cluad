package com.gcgenome.test

import com.gcgenome.lims.interpretable.kokr.HgvscPhraseKoKr
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(SpringExtension::class)
internal class HgvscPhraseKoKrTest {
    @Test @DisplayName("c.623T>G")
    internal fun test1() {
      assertEquals(HgvscPhraseKoKr.explainHgvsc("c.623T>G"), "623번째 위치한 염기서열 T가 G로 치환")
    }
    @Test @DisplayName("c.1009-7T>G")
    internal fun test2() {
        assertEquals(HgvscPhraseKoKr.explainHgvsc("c.1009-7T>G"), "exon의 첫 번째 위치한 염기인 c.1009로부터 upstream 방향으로 7번째 염기인 T가 G로 치환")
    }
    @Test @DisplayName("c.659+1G>A")
    internal fun test3() {
        assertEquals(HgvscPhraseKoKr.explainHgvsc("c.659+1G>A"), "exon의 말단에 위치한 염기인 c.659로부터 downstream 방향으로 1번째 염기인 G가 A로 치환")
    }
    @Test @DisplayName("c.5136+4_5136+7del")
    internal fun test4() {
        assertEquals(HgvscPhraseKoKr.explainHgvsc("c.5136+4_5136+7del"), "exon의 말단에 위치한 염기인 c.5136로부터 downstream 방향으로 4번째 염기부터 7번째 염기까지 삭제")
    }
    @Test @DisplayName("c.5136+4_5199-35del")
    internal fun test5() {
        assertEquals(HgvscPhraseKoKr.explainHgvsc("c.5136+4_5199-35del"), "exon의 말단에 위치한 염기인 c.5136로부터 downstream 방향으로 4번째 염기부터 c.5199의 upstream 방향으로 35번째 염기까지 삭제")
    }
    @Test @DisplayName("c.3627dup, c.27del")
    internal fun test6() {
        assertEquals(HgvscPhraseKoKr.explainHgvsc("c.3627dup"), "3627번째 위치한 염기가 중복")
        assertEquals(HgvscPhraseKoKr.explainHgvsc("c.27del"), "27번째 위치한 염기가 삭제")
    }
    @Test @DisplayName("c.5576_5579del, c.123_124dup")
    internal fun test7() {
        assertEquals(HgvscPhraseKoKr.explainHgvsc("c.5576_5579del"), "5576번째 염기부터 5579번째 염기까지 삭제")
        assertEquals(HgvscPhraseKoKr.explainHgvsc("c.123_124dup"), "123번째 염기부터 124번째 염기까지 중복")
    }
    @Test @DisplayName("fail")
    internal fun test8() {
        assertNull(HgvscPhraseKoKr.explainHgvsc("missense"))
    }
}
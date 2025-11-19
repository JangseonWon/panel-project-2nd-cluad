package com.gcgenome.lims.interpretable.kokr

import java.util.*
import java.util.function.Function

class HgvscPhraseKoKr {
    @Suppress("RegExpRedundantEscape")
    companion object {
        private data class PatternComment(
            private val pattern: Regex,
            private val processor: Function<MatchResult, String>
        ) {
           fun get(param: String): String? {
               val match = pattern.find(param)
               return if(match!=null) processor.apply(match) else null
           }
        }
        private val PATTERN_HGVSC1 = PatternComment("^(\\d+)([A-Z])>([A-Z])$".toRegex()) {
            String.format("%s번째 위치한 염기서열 %s가 %s로 치환", it.groupValues[1], it.groupValues[2], it.groupValues[3])
        }
        private val PATTERN_HGVSC2 = PatternComment("^(\\d+)([+-])(\\d+)([A-Z])>([A-Z])$".toRegex()) {
            val (pos, stream) = if ("+" == it.groupValues[2]) Pair("말단에", "downstream") else Pair("첫 번째", "upstream")
            String.format("exon의 %s 위치한 염기인 c.%s로부터 %s 방향으로 %s번째 염기인 %s가 %s로 치환", pos, it.groupValues[1], stream, it.groupValues[3], it.groupValues[4], it.groupValues[5])
        }
        private val PATTERN_HGVSC3 = PatternComment("^(\\d+)([+-])(\\d+)\\_(\\d+)([+-])(\\d+)(del|dup)$".toRegex()) {
            val (pos1, stream1) = if ("+" == it.groupValues[2]) Pair("말단에", "downstream") else Pair("첫 번째", "upstream")
            val deldup = if ("del" == it.groupValues[7]) "삭제" else "중복"
            if(it.groupValues[1] == it.groupValues[4] && it.groupValues[2] == it.groupValues[5])
                String.format("exon의 %s 위치한 염기인 c.%s로부터 %s 방향으로 %s번째 염기부터 %s번째 염기까지 %s",
                pos1, it.groupValues[1], stream1,it.groupValues[3], it.groupValues[6], deldup)
            else String.format("exon의 %s 위치한 염기인 c.%s로부터 %s 방향으로 %s번째 염기부터 c.%s의 %s 방향으로 %s번째 염기까지 %s",
                pos1, it.groupValues[1], stream1, it.groupValues[3],
                it.groupValues[4], if ("+" == it.groupValues[5]) "downstream" else "upstream", it.groupValues[6],
                deldup)
        }
        private val PATTERN_HGVSC4 = PatternComment("^(\\d+)(del|dup)$".toRegex()) {
            String.format("%s번째 위치한 염기가 %s", it.groupValues[1], if ("del" == it.groupValues[2]) "삭제" else "중복")
        }
        private val PATTERN_HGVSC5 = PatternComment("^(\\d+)\\_(\\d+)(del|dup)$".toRegex()) {
            String.format("%s번째 염기부터 %s번째 염기까지 %s", it.groupValues[1], it.groupValues[2], if ("del".equals(it.groupValues[3])) "삭제" else "중복")
        }
        private val PATTERN_HGVSC6 = PatternComment("^(\\d+)\\_(\\d+)ins([A-Z]+)$".toRegex()) {
            String.format("%s번째 염기부터 %s번째 염기 사이에 %s가 삽입", it.groupValues[1], it.groupValues[2], it.groupValues[3])
        }
        private val PATTERN_HGVSC7 = PatternComment("^(\\d+)\\_(\\d+)delins([A-Z]+)$".toRegex()) {
            String.format("%s번째 염기부터 %s번째 염기가 삭제되고 %s가 삽입", it.groupValues[1], it.groupValues[2], it.groupValues[3])
        }
        private val PATTERNS_HGVSC = listOf(PATTERN_HGVSC1, PATTERN_HGVSC2, PATTERN_HGVSC3, PATTERN_HGVSC4, PATTERN_HGVSC5, PATTERN_HGVSC6, PATTERN_HGVSC7)
        @Suppress("NAME_SHADOWING")
        fun explainHgvsc(hgvsc: String): String? {
            if(hgvsc.isEmpty()) return null
            val hgvsc = hgvsc.removePrefix("c.")
            return PATTERNS_HGVSC.stream().map { it.get(hgvsc) }.filter(Objects::nonNull).findFirst().orElse(null)
        }
    }
}
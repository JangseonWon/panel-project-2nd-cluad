package com.gcgenome.lims.interpretable.kokr

import com.google.common.base.Splitter
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

internal class HgvspPhraseKoKr {
    @Suppress("RegExpRedundantEscape", "NonAsciiCharacters")
    companion object {
        private const val AMINO_ACID = "(?!Ter)[A-Z][a-z]{2}|[A-Z]{1}"
        enum class Effect {
            Nonsense, Missense, StopRetain, StopLoss, Inframe, StartLost, Synonymus, Frameshift
        }
        private data class PatternComment(
            private val effect: Effect,
            private val pattern: Regex,
            private val processor: Function<MatchResult, String>
        ) {
            fun get(param: String): String? {
                val match = pattern.find(param)
                return if (match != null) processor.apply(match) else null
            }
        }
        private val Nonsense = PatternComment(Effect.Nonsense, "^($AMINO_ACID)(\\d+)(?:Ter|\\*)$".toRegex()) {
            val aa1 = parseAACodeToName(it.groupValues[1])
            String.format("%s번째 아미노산인 %s%s stop codon으로 치환", it.groupValues[2], aa1, Korean.aminoAcid한글조사(aa1, "이", "가"))
        }
        private val Missense = PatternComment(Effect.Missense, "^($AMINO_ACID)(\\d+)($AMINO_ACID)\$".toRegex()) {
            val aa1: String = parseAACodeToName(it.groupValues[1])
            val aa2: String = parseAACodeToName(it.groupValues[3])
            String.format("%s번째 아미노산인 %s%s %s%s로 치환", it.groupValues[2], aa1, Korean.aminoAcid한글조사(aa1, "이", "가"), aa2, Korean.aminoAcid한글조사(aa2, "으", ""))
        }
        private val StopRetain = PatternComment(Effect.StopRetain, "^(?:Ter|\\*)(\\d+)\\=\$".toRegex()) {   // Ter숫자=
            String.format("%s번 코돈인 종결 코돈이 그대로 보전", it.groupValues[1])
        }
        private val StopLoss = PatternComment(Effect.StopLoss, "^(?:Ter|\\*)(\\d+)($AMINO_ACID)ext(Ter|\\*)([\\d?]+)\$".toRegex()) {   // Ter숫자AAextTer[숫자혹은물음표]
            val aa1 = parseAACodeToName(it.groupValues[2])
            String.format("%s번 코돈인 종결코돈이 %s로 치환되면서 단백질 길이가 연장", it.groupValues[1], aa1, Korean.aminoAcid한글조사(aa1, "으", ""))
        }
        private val Inframe1 = PatternComment(Effect.Inframe, "^($AMINO_ACID)(\\d+)(del|dup)\$".toRegex()) {   // AA숫자d
            val aa1 = parseAACodeToName(it.groupValues[1])
            if ("del".equals(it.groupValues[3], ignoreCase = true))
                String.format("%s번째 아미노산인 %s%s 제거 되면서 단백질 길이가 축소", it.groupValues[2], aa1, Korean.aminoAcid한글조사(aa1, "이", "가"))
            else String.format("%s번째 아미노산인 %s%s 중복", it.groupValues[2], aa1, Korean.aminoAcid한글조사(aa1, "이", "가"))
        }
        private val Inframe2 = PatternComment(Effect.Inframe, "^($AMINO_ACID)(\\d+)_($AMINO_ACID)(\\d+)(del|dup)\$".toRegex()) {   // AA숫자_AA숫자d
            val aa1 = parseAACodeToName(it.groupValues[1])
            if ("del".equals(it.groupValues[5], ignoreCase = true))
                String.format("%s번째 아미노산인 %s%s로부터 %s번째 아미노산인 %s까지 제거 되면서 단백질 길이가 축소", it.groupValues[2], aa1, Korean.aminoAcid한글조사(aa1, "으", ""), it.groupValues[4], parseAACodeToName(it.groupValues[3]))
            else String.format("%s번째 아미노산인 %s%s로부터 %s번째 아미노산인 %s까지 한번 더 중복", it.groupValues[2], aa1, Korean.aminoAcid한글조사(aa1, "으", ""), it.groupValues[4], parseAACodeToName(it.groupValues[3]))
        }
        private val Inframe3 = PatternComment(Effect.Inframe, "^($AMINO_ACID)(\\d+)_($AMINO_ACID)(\\d+)delins($AMINO_ACID)\$".toRegex()) {   //Inframe_delins: AA숫자_AA숫자delinsAA
            val aa1 = parseAACodeToName(it.groupValues[1])
            val aa3 = parseAACodeToName(it.groupValues[5])
            String.format("%s번째 아미노산인 %s%s로부터 %s번째 아미노산인 %s까지 제거되고 그 위치에 %s%s 삽입", it.groupValues[2], aa1, Korean.aminoAcid한글조사(aa1, "으", ""),
                it.groupValues[4], parseAACodeToName(it.groupValues[3]),
                aa3, Korean.aminoAcid한글조사(aa3, "이", "가"))
        }
        private val Inframe4 = PatternComment(Effect.Inframe, "^($AMINO_ACID)(\\d+)_($AMINO_ACID)(\\d+)ins((?:$AMINO_ACID)+)\$".toRegex()) {   //Inframe_dup type3: AA숫자_AA숫자ins[AA여러개]
            val aa1 = parseAACodeToName(it.groupValues[1])
            val aa3 = Splitter.fixedLength(3).splitToList(it.groupValues[5]).stream().map(Companion::parseAACodeToName).collect(Collectors.joining(", "))
            String.format("%s번째 아미노산인 %s%s로부터 %s번째 아미노산인 %s사이에 %s%s 삽입", it.groupValues[2], aa1, Korean.aminoAcid한글조사(aa1, "으", ""),
                it.groupValues[4], parseAACodeToName(it.groupValues[3]),
                aa3, Korean.aminoAcid한글조사(aa3, "이", "가"))
        }
        private val StartLost = PatternComment(Effect.StartLost, "^($AMINO_ACID)1\\?\$".toRegex()) {
            String.format("첫번째 아미노산인 %s의 시작코돈이 변경", parseAACodeToName(it.groupValues[1]))
        }
        private val Synonymus = PatternComment(Effect.Synonymus, "^($AMINO_ACID)(\\d+)=\$".toRegex()) {
            val aa1 = parseAACodeToName(it.groupValues[1])
            String.format("%s번째 아미노산인 %s%s 그대로 보존", it.groupValues[2], aa1, Korean.aminoAcid한글조사(aa1, "이", "가"))
        }
        private val Frameshift1 = PatternComment(Effect.Frameshift, "^($AMINO_ACID)(\\d+)($AMINO_ACID)fs(Ter|\\*)*(\\d+)?\$".toRegex()) {
            val aa1 = parseAACodeToName(it.groupValues[1])
            val aa2 = parseAACodeToName(it.groupValues[3])
            val pos = if(it.groupValues[5].isNotEmpty()) it.groupValues[5] + "번째 " else ""
            String.format("%s번째 아미노산인 %s%s %s%s로 치환되고 해석 틀이 변하여 다음 %s아미노산이 stop codon으로 변경", it.groupValues[2], aa1, Korean.aminoAcid한글조사(aa1, "이", "가"),
            aa2, Korean.aminoAcid한글조사(aa2, "으", ""), pos)
        }
        private val Frameshift2 = PatternComment(Effect.Frameshift, "^(?:Ter|\\*)(\\d+)($AMINO_ACID)fs(Ter|\\*)*(\\d+)\$".toRegex()) {
            val aa1 = parseAACodeToName(it.groupValues[2])
            String.format("%s번째 Stop codon이 %s%s로 치환되고 해석 틀이 변하여 다음 %s번째 아미노산이 stop codon으로 변경", it.groupValues[1],
                aa1, Korean.aminoAcid한글조사(aa1, "으", ""), it.groupValues[4])
        }
        private val PATTERNS_HGVSP = listOf(
            Nonsense, Missense, StopRetain, StopLoss, Inframe1, Inframe2, Inframe3, Inframe4, StartLost, Synonymus, Frameshift1, Frameshift2
        )
        @Suppress("NAME_SHADOWING")
        fun explainHgvsp(hgvsc: String): String? {
            if(hgvsc.isEmpty()) return null
            val hgvsp = hgvsc.removePrefix("p.")
            return PATTERNS_HGVSP.stream().map { it.get(hgvsp) }.filter(Objects::nonNull).findFirst().orElse(null)
        }
        private fun parseAACodeToName(code: String): String {
            if (code.length == 1) return parseAACodeToName(parseAACodeTo3Digit(code))
            when (code) {
                "Ala" -> return "Alanine"
                "Arg" -> return "Arginine"
                "Asn" -> return "Asparagine"
                "Asp" -> return "Aspartic acid"
                "Cys" -> return "Cysteine"
                "Glu" -> return "Glutamic acid"
                "Gln" -> return "Glutamine"
                "Gly" -> return "Glycine"
                "His" -> return "Histidine"
                "Ile" -> return "Isoleucine"
                "Leu" -> return "Leucine"
                "Lys" -> return "Lysine"
                "Met" -> return "Methionine"
                "Phe" -> return "Phenylalanine"
                "Pro" -> return "Proline"
                "Ser" -> return "Serine"
                "Thr" -> return "Threonine"
                "Trp" -> return "Tryptophan"
                "Tyr" -> return "Tyrosine"
                "Val" -> return "Valine"
            }
            throw RuntimeException()
        }
        private fun parseAACodeTo3Digit(c: String): String {
            when (c) {
                "A" -> return "Ala"
                "R" -> return "Arg"
                "N" -> return "Asn"
                "D" -> return "Asp"
                "C" -> return "Cys"
                "E" -> return "Glu"
                "Q" -> return "Gln"
                "G" -> return "Gly"
                "H" -> return "His"
                "I" -> return "Ile"
                "L" -> return "Leu"
                "K" -> return "Lys"
                "M" -> return "Met"
                "F" -> return "Phe"
                "P" -> return "Pro"
                "S" -> return "Ser"
                "T" -> return "Thr"
                "W" -> return "Trp"
                "Y" -> return "Tyr"
                "V" -> return "Val"
                "*" -> return "Ter"
            }
            throw RuntimeException()
        }
        private fun Korean.Companion.aminoAcid한글조사(aa: String, 이: String, 가: String): String =
            if (aa.endsWith("ne") || aa.endsWith("phan")) 이 else 가
    }
}
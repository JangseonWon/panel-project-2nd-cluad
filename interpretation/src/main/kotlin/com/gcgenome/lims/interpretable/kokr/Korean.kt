package com.gcgenome.lims.interpretable.kokr

@Suppress("NonAsciiCharacters", "LocalVariableName")
internal class Korean {
    companion object {
        fun 한글조사(주어: String, 이을은와: String, 가를는과: String): String {
            val 종성 = 주어[주어.length - 1]
            if (종성.code < 0xAC00 || 종성.code > 0xD7A3) return 가를는과
            val 조사 = if ((종성.code - 0xAC00) % 28 > 0) 가를는과 else 이을은와
            return 주어 + 조사
        }
    }
}
package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.dto.InterpretationPanel
import com.gcgenome.lims.interpretable.InterpretationHeaderPhrase


/*
 * Single 문구와 차이점
 * 1. Single은 "(타겟 유전자명) 유전자에서"로 시작하고, GenePlus는 "(타겟 유전자명) 유전자 분석 결과," 형식을 사용
 * 2. Single은 변이 유형을 "정식 명칭 (약어)" 형태로 표기하며, GenePlus는 약어만 사용
 */
internal class InterpretationHeaderPhraseGenePlusKoKr: InterpretationHeaderPhrase {
    override fun result(variants: List<Pair<InterpretationPanel.Variant, List<Disease>>>): String {
        if(variants.isEmpty()) return "질환과 관련된 변이는 발견되지 않았습니다."
        val clazzes = variants.map { it.first.clazz }.toSet()
        val prefix = StringBuilder("%p 분석 결과, ")
        val summary = clazzes.sorted().joinToString("와 ") { it.simpleName } + "가 발견되었"
        prefix.append(summary)
        prefix.append("습니다.")
        return prefix.toString()
    }
}
package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.constants.Clazz
import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.dto.InterpretationPanel
import com.gcgenome.lims.interpretable.InterpretationHeaderPhrase
import java.util.stream.Collectors

internal class InterpretationHeaderPhraseSimpleKoKr: InterpretationHeaderPhrase {
    override fun result(variants: List<Pair<InterpretationPanel.Variant, List<Disease>>>): String {
        if(variants.isEmpty()) return "질환과 관련된 변이는 발견되지 않았습니다."
        val hit: MutableMap<Clazz, List<String>> = variants.stream().map { it.first }
            .collect(Collectors.groupingBy({ it.clazz }, Collectors.mapping({ it.gene }, Collectors.toList())))
        val prefix = StringBuilder("%p 분석 결과, ")
        val variantsText = hit.toList().sortedBy { it.first }.joinToString("고, ") { (clazz, variants) ->
            variants.distinct().joinToString(", ", postfix = " 유전자에서 ${clazz}가 발견되었")
        }
        prefix.append(variantsText)
        prefix.append("습니다.")
        return prefix.toString()
    }
}
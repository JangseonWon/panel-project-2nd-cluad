package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.constants.Clazz
import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.dto.InterpretationPanel
import com.gcgenome.lims.interpretable.InterpretationHeaderPhrase
import java.util.stream.Collectors

internal class InterpretationHeaderPhraseSingleKoKr: InterpretationHeaderPhrase {
    override fun result(variants: List<Pair<InterpretationPanel.Variant, List<Disease>>>): String {
        if(variants.isEmpty()) return "질환과 관련된 변이는 발견되지 않았습니다."
        val hit: MutableMap<String, Set<Clazz>> = variants.stream().map { it.first }.collect(
            Collectors.groupingBy({ it.gene },
                Collectors.mapping({ it.clazz }, Collectors.toSet())))
        val prefix = StringBuilder("")
        val summary = hit.map { (gene, clazzes) -> "$gene 유전자에서 " + clazzes.sorted()
            .joinToString("와 ") { "${it.fullName} (${it.simpleName})" } + "가 발견되었"
        }.joinToString("고, ")
        prefix.append(summary + "습니다.")
        return prefix.toString()
    }
}
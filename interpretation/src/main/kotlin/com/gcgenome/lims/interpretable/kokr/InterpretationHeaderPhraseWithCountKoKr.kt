package com.gcgenome.lims.interpretable.kokr

/*
 * InterpretationHeaderPhraseSimpleKoKr 에 유전자 개수까지 포함된 헤더.
 * 사용하지 않음
internal class InterpretationHeaderPhraseWithCountKoKr: InterpretationHeaderPhrase {
    fun result(variants: List<Pair<InterpretationPanel.Variant, List<Disease>>>): String {
        if(variants.isEmpty()) return "질환과 관련된 변이는 발견되지 않았습니다."
        val hit: MutableMap<String, List<String>> = variants.stream().map { it.first }.collect(
            Collectors.groupingBy({ it.clazz },
                Collectors.mapping({ it.gene }, Collectors.toList())))
        val prefix = StringBuilder("%p 분석 결과, ")
        val withCount = hit.values.stream().anyMatch{ it.size > 1 }
        for(variant in variants.stream().map { it.first }.toList()) {
            if(!hit.containsKey(variant.clazz)) continue
            val list = hit[variant.clazz]!!
            hit.remove(variant.clazz)
            prefix.append(list.stream().distinct().collect(Collectors.joining(", "))).append(" 유전자에서 ").append(variant.clazz).append("가 ")
            if(withCount) prefix.append(list.size).append("개 ")
            prefix.append("발견되었")
            if(hit.isEmpty()) prefix.append("습니다.")
            else prefix.append("고, ")
        }
        return prefix.toString()
    }
}*/
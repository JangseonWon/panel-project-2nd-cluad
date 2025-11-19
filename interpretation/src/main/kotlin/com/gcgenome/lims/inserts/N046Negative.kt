package com.gcgenome.lims.inserts

import com.gcgenome.lims.test.RareDiseasePanel
import org.springframework.stereotype.Component

@Component
class N046Negative : Insert() {
    private val NEGATIVE_FORMAT = "%p 분석 결과, 질환과 관련된 변이는 발견되지 않았습니다."
    private val serviceSet = RareDiseasePanel.values().map { it.code }.toSet()

    override fun weight(): Int = 0

    override fun text(something: Any?): String {
        something as String
        return NEGATIVE_FORMAT.replace("%p", something.replace("검사", ""))
    }

    override fun services(): Set<String> = setOf("N046")

    override fun position(): Position = Position.NEGATIVE
}
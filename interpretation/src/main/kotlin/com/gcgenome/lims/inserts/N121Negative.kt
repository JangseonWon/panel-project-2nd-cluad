package com.gcgenome.lims.inserts

import org.springframework.stereotype.Component

@Component
open class N121Negative : Insert() {
    private val services = setOf("N121")
    override fun weight(): Int = Int.MAX_VALUE

    override fun text(something: Any?): String = """
        [추가소견]
        APOB, LDLRAP1, PCSK9 유전자의 모든 exon과 인접 intron의 염기서열을 분석한 결과, 질환 관련 변이는 발견되지 않았습니다.
        """.trimIndent()

    override fun services(): Set<String> = services

    override fun position(): Position = Position.NEGATIVE
}
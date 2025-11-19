package com.gcgenome.lims.inserts

import org.springframework.stereotype.Component

@Component
class R2200302Negative : Insert() {
    private val NEGATIVE_FORMAT = "Gaucher disease 관련 GLA 유전자에서 질환 관련 변이가 발견되지 않았습니다."
    override fun weight(): Int = 0
    override fun text(something: Any?): String = NEGATIVE_FORMAT
    override fun services(): Set<String> = setOf("R2200302")
    override fun position(): Position = Position.NEGATIVE
}
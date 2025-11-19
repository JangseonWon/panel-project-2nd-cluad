package com.gcgenome.lims.inserts

import org.springframework.stereotype.Component

@Component
class N055PostSuffix : Insert() {
    private val POSTFIX_SMT_FORMAT = "* CMT의 가장 흔한 원인인 CMT1A (PMP22) duplication 소견은 관찰되지 않았습니다."
    override fun weight(): Int = 0

    override fun text(something : Any?): String = POSTFIX_SMT_FORMAT

    override fun services(): Set<String> = setOf("N055")
    override fun position(): Position = Position.POSTSUFFIX
}

package com.gcgenome.lims.inserts

class N122Negative : Insert(){
    private val services = setOf("N122")
    override fun weight(): Int = 0

    override fun text(something: Any?): String = "미토콘드리아 유전자 패널 분석 결과, 질환과 관련된 변이는 발견되지 않았습니다."

    override fun services(): Set<String> = services

    override fun position(): Position = Position.NEGATIVE
}
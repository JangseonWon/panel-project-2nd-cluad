package com.gcgenome.lims.inserts

import com.gcgenome.lims.test.GenePlusPanel
import org.springframework.stereotype.Component

@Component
class GenePlusNegativeDefault : Insert() {
    // GenePlus는 RareDisease와 같으나, 엑손 영역은 시퀀싱이 잘 되어 관련 문구가 없다.
    private val NEGATIVE_FORMAT = """
        %p 분석 결과, 질환과 관련된 변이는 발견되지 않았습니다.

        * 본 검사는 검사에 포함된 유전자들의 coding exon과 인접 intron 영역을 분석하며, deep intronic variant, exon deletion/duplication, genomic rearrangement를 포함한 copy number variant 검출은 제한적입니다. 검사의 기술적 한계에 대한 자세한 내용은 검사의 한계를 참조하십시오.
        """.trimIndent()
    // BRCA는 빼고
    private val serviceSet = GenePlusPanel.values().filter { (it != GenePlusPanel.S096) && (it != GenePlusPanel.S097) }.map { it.code }.toSet()

    override fun weight(): Int = 100

    override fun text(something: Any?): String {
        something as String
        return NEGATIVE_FORMAT.replace("%p", "${something.split("gene")[0]}유전자")
    }

    override fun services(): Set<String> = serviceSet

    override fun position(): Position = Position.NEGATIVE
}
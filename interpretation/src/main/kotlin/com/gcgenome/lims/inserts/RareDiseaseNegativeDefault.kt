package com.gcgenome.lims.inserts

import com.gcgenome.lims.test.RareDiseasePanel
import org.springframework.stereotype.Component

@Component
class RareDiseaseNegativeDefault : Insert() {
    private val NEGATIVE_FORMAT = """%p 분석 결과, 질환과 관련된 변이는 발견되지 않았습니다.

* 본 검사는 검사에 포함된 유전자들의 coding exon과 인접 intron 영역을 분석하며, deep intronic variant, exon deletion/duplication, genomic rearrangement를 포함한 copy number variant 검출은 제한적입니다. 또한, GC 함량이 높거나 염기서열 상동성이 높은 일부 유전자의 exon 부위는 본 검사법으로 변이를 검출하는데 한계가 있습니다. 검사의 기술적 한계에 대한 자세한 내용은 검사의 한계를 참조하십시오.
        """.trimIndent()
    private val serviceSet = RareDiseasePanel.values().map { it.code }.toSet()

    override fun weight(): Int = 100

    override fun text(something: Any?): String {
        something as String
        return NEGATIVE_FORMAT.replace("%p", something.replace("검사", ""))
    }

    override fun services(): Set<String> = serviceSet

    override fun position(): Position = Position.NEGATIVE
}
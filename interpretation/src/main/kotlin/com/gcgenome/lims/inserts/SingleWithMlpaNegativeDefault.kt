package com.gcgenome.lims.inserts

import com.gcgenome.lims.test.SingleGenePanel
import com.gcgenome.lims.test.SingleGenePanelWithMlpa
import org.springframework.stereotype.Component

@Component
class SingleWithMlpaNegativeDefault : Insert() {
    private val NEGATIVE_FORMAT = "%g의 모든 exon과 인접 intron의 염기서열을 분석한 결과, 질환 관련 변이는 발견되지 않았습니다. \n %g의 exon deletion/duplication을 확인하기 위해 MLPA 검사를 시행한 결과, deletion/duplication 소견이 관찰되지 않았습니다."
    private val serviceSet = SingleGenePanelWithMlpa.values().map { it.singleGenePanel.code }.toSet() + SingleGenePanelWithMlpa.values().map { it.code() }.toSet()
    override fun weight(): Int = 110
    override fun text(something: Any?): String {
        something as String
        return NEGATIVE_FORMAT.replace("%g", "${something.split("gene")[0]}유전자")
    }
    override fun services(): Set<String> = serviceSet
    override fun position(): Position = Position.NEGATIVE
}
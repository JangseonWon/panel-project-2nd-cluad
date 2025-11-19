package com.gcgenome.lims.inserts

import org.springframework.stereotype.Component

@Component
class N046PostSuffix : Insert() {
    override fun weight(): Int = 0

    override fun text(something : Any?): String = "* 본 검사는 검사에 포함된 유전자들의 coding exon과 인접 intron 영역을 분석하며, deep intronic variant, exon deletion/duplication, genomic rearrangement를 포함한 copy number variant 검출은 제한적입니다. 또한, SCA1 (ATXN1), SCA2 (ATXN2), SCA3 (ATXN3), SCA6 (CACNA1A), SCA7 (ATXN7), SCA8 (ATXN8), SCA10 (ATXN10), SCA12 (PPP2R2B), SCA17 (TBP), SCA31, SCA36 (NOP56), DRPLA (ATN1) 등 Spinocerebellar ataxia (SCA)의 주요 원인인 반복서열 증가를 검출할 수 없으며, GC 함량이 높거나 염기서열 상동성이 높은 일부 유전자의 exon 부위는 본 검사법으로 변이를 검출하는데 한계가 있습니다. 검사의 기술적 한계에 대한 자세한 내용은 검사의 한계를 참조하십시오."

    override fun services(): Set<String> = setOf("N046")
    override fun position(): Position = Position.POSTSUFFIX
}

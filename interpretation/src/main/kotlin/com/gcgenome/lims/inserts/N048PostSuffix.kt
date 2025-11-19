package com.gcgenome.lims.inserts

import org.springframework.stereotype.Component

@Component
class N048PostSuffix : Insert() {
    private val POSTFIX_DSD_FORMAT =
        "* Congenital Adrenal Hyperplasia (CAH)의 가장 흔한 원인 중 하나인 21-hydroxylase deficiency CAH (21-OHD)의 경우 " +
                "원인 유전자인 CYP21A2 유전자와 염기서열이 매우 유사한 Pseudogene (CYP21A1P)으로 인하여 NGS Panel 검사의 질환 관련 변이 " +
                "분석이 제한적입니다. 따라서, 21-OHD가 의심될 경우 별도로 CYP21A2-specific Sequencing 및 MLPA (multiplex ligation-dependent " +
                "probe amplification) 검사 시행이 권장됩니다."
    override fun weight(): Int = 0

    override fun text(something : Any?): String = POSTFIX_DSD_FORMAT

    override fun services(): Set<String> = setOf("N048")

    override fun position(): Position = Position.POSTSUFFIX
}

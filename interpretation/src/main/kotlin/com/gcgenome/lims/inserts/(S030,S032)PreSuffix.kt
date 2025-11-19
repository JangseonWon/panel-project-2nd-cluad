package com.gcgenome.lims.inserts

import org.springframework.stereotype.Component

@Component
class `(S030,S032)PreSuffix` : Insert() {
    override fun weight(): Int = 0

    override fun text(something : Any?): String {
        return "- Mature protein:\n" +
                "- Reported severity:\n" +
                "- History of inhibitor:\n" +
                "- Reference ID:\n" +
                "- Year reported:"
    }

    override fun services(): Set<String> = setOf("S030", "S032")

    override fun position(): Position = Position.PRESUFFIX
}
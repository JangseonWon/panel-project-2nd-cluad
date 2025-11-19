package com.gcgenome.lims.projection

import io.r2dbc.postgresql.codec.Json
import java.time.LocalDateTime

data class Interpretation (
    val sample: Long,
    val service: String,
    val createBy: User,
    val createAt: LocalDateTime,
    val lastModifyBy: User,
    val lastModifyAt: LocalDateTime,
    val value: String?
) {
    companion object {
        data class InterpretationBuilder(
            val sample: Long,
            val service: String,
            val creatorId: String,
            val creator: String,
            val createAt: LocalDateTime,
            val lastModifierId: String,
            val lastModifier: String,
            val lastModifyAt: LocalDateTime,
            val json: Json?
        ) {
            fun build() = Interpretation(sample, service, User(creatorId, creator), createAt, User(lastModifierId, lastModifier), lastModifyAt, json?.asString())
        }
    }
}
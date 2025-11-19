package com.gcgenome.lims.inserts

import org.springframework.stereotype.Component

@Component
class N121PostSuffix : N121Negative() {
    override fun position(): Position = Position.POSTSUFFIX
}
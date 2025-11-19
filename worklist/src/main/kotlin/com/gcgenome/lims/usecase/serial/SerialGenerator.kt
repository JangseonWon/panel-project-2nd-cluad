package com.gcgenome.lims.usecase.serial

import com.gcgenome.lims.domain.Serial
import com.gcgenome.lims.domain.Work
import reactor.core.publisher.Flux

interface SerialGenerator {
    // 시리얼이 없는 워크 목록을 받아서, 채울 수 있는 워크의 시리얼을 반환한다.
    fun generate(works: List<Work>): Flux<Pair<Work, Serial>>
}
package com.gcgenome.lims.usecase.serial.generator

import com.gcgenome.lims.domain.Serial
import com.gcgenome.lims.domain.Work
import com.gcgenome.lims.usecase.serial.PreviousWorkRepository
import com.gcgenome.lims.usecase.serial.SerialGenerator
import com.github.f4b6a3.ulid.Ulid
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.stream.Collectors

@Order(1)
@Component
class RetestSerialGenerator(private val repo: PreviousWorkRepository): SerialGenerator {
    override fun generate(works: List<Work>): Flux<Pair<Work, Serial>> {
        val workMap = works.stream().collect(Collectors.toMap({ it.key() }, { it }, { existing, replacement ->
            val s1 = existing.createAt
            val s2 = replacement.createAt
            if(s1 > s2) existing else replacement
        }))
        return repo.findPrevious(works).groupBy { it.key() }
            .flatMap { grouped -> grouped.reduce { work1, work2 -> // 각 그룹 내에서 가장 최신 createAt 선택
                if (work1.createAt > work2.createAt) work1 else work2
            } }.mapNotNull {
            if(!workMap.containsKey(it.key())) null
            else {
                val work = workMap[it.key()]!!
                val updatedSerial = generateUpdatedSerial(it.serial ?: throw IllegalArgumentException("serial is null: $it"))
                val serial = Serial(
                    id = Ulid.fast().toUuid(),
                    worklist = work.worklist,
                    index = work.index,
                    serial = updatedSerial,
                    infix = it.infix ?: throw IllegalArgumentException("infix is null: $it"),
                    idx = it.suffix ?: throw IllegalArgumentException("suffix is null: $it"),
                )
                Pair(work, serial)
            }
        }
    }
    private fun Work.key() = requests.map { it.sample.id }.joinToString(",")
    private fun generateUpdatedSerial(baseSerial: String): String = if (baseSerial.matches(Regex(".*-R(\\d+)?$"))) {
        val currentNumber = Regex(".*-R(\\d+)?$").find(baseSerial)?.groupValues?.get(1)?.toIntOrNull() ?: 1
        baseSerial.replace(Regex("-R(\\d+)?$"), "-R${currentNumber + 1}")
    } else "$baseSerial-R" // -R 추가
}
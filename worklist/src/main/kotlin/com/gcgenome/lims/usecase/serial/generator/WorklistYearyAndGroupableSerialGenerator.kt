package com.gcgenome.lims.usecase.serial.generator

import com.gcgenome.lims.domain.Serial
import com.gcgenome.lims.domain.Work
import com.gcgenome.lims.test.*
import com.gcgenome.lims.usecase.serial.SerialGenerator
import com.gcgenome.lims.usecase.serial.SerialRepository
import com.github.f4b6a3.ulid.Ulid
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.time.ZoneId
import java.time.ZonedDateTime


// 한 워크리스트 안에 여러 종류의 그룹이 존재한다.
// 각각의 그룹별로 인덱스를 카운팅한다.
@Order(LOWEST_PRECEDENCE)
@Component
class WorklistYearyAndGroupableSerialGenerator(private val repo: SerialRepository): SerialGenerator {
    override fun generate(works: List<Work>): Flux<Pair<Work, Serial>> {
        val current = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        val currentYearStart = current.withMonth(1)
            .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        val findIndices = repo.findLatest(currentYearStart).collectList()
            .defaultIfEmpty(emptyList())
            .map { latest -> latest.toMap() }
        val year = (current.year % 100).toShort()

        return findIndices.flatMapMany {
            val indexes = it.toMutableMap()
            Flux.fromIterable(works)
                .sort(Comparator.comparing(Work::index))
                .map { work -> work to work.createSerial(groupByService, indexes, year) }
        }
    }
    private fun Work.createSerial(groupByService: Map<String, String>, indexes: MutableMap<String, Short>, year: Short): Serial {
        val serviceGroups = requests
            .map { it.service.id }
            .mapNotNull { groupByService[it] }
            .distinct()
            .toList()
        if(serviceGroups.size != 1) throw IllegalArgumentException("Work에 연결된 모든 의뢰정보는 동일한 서비스 그룹에 속해야 합니다: ${requests.map { it.service.id }}")
        val infix = serviceGroups.first()
        val idx = (indexes[infix]?:0).plus(1).toShort()
        indexes[infix] = idx
        val serial = generateSerial(year, infix, idx)
        return toSerial(infix, idx, serial)
    }
    private fun generateSerial(year: Short, infix: String, idx: Short) = "%02d%s%03d".format(year, infix, idx)
    private fun Work.toSerial(infix: String, idx: Short, serial: String) = Serial(
        id = Ulid.fast().toUuid(),
        worklist = worklist,
        index = index,
        serial = serial,
        infix = infix,
        idx = idx
    )
    companion object {
        class SerialCodeAdapter<T> (
            private val original: T,
        ) : HasSerialGroup, HasCode where T : HasSerialGroup, T : HasCode {
            override fun serialGroup(): String = original.serialGroup()
            override fun code(): String = original.code()
        }

        val groupByService = (
                BloodCancerPanel.values() + Des.values() + `FLT3-ITD`.values()  + GenePlusPanel.values() +
                GenomeScreen.values() + Hrd.values() + NonTSO.values() + RareDiseasePanel.values() + Sanger.values() +
                SingleGenePanel.values() + SingleGenePanelWithMlpa.values() + SolidTumorPanel.values() + Wes.values() +
                WesWithSingleGene.values()
        ).map { SerialCodeAdapter(it) }.associate { it.code() to it.serialGroup() }
    }
}
package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.entity.Analysis
import com.gcgenome.lims.interpretable.Interpretable
import com.gcgenome.lims.ssh.SshClient
import com.gcgenome.lims.test.Hrd
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.extra.math.max

@Component
class Hrd(val om: ObjectMapper, val repo: AnalysisRepository): Interpretable {
    private val client =  SshClient("172.19.207.52", 22, "atk122", "123", 9999, 1006)
    override fun chk(sample: Long, service: String): Boolean = Hrd.values().stream().map{ t->t.code() }.anyMatch(service::equals)

    override fun interpret(sample: Long, service: String, param: Map<*, *>): Mono<*> =
        repo.findAnalysis(sample, service)
            .max(Comparator.comparing(Analysis::createAt))
            .flatMap { analysis ->
                val batch = analysis.batch
                val serial = analysis.serial
                val dest = "/storm/Analysis/HRD/OncoHRD/${batch}/interpretation/${serial}_Tier_annot.json"
                val output = "/storm/Analysis/HRD/OncoHRD/${batch}/interpretation/${serial}_interpret.txt"
                interpret(param, dest, output, batch, serial)
            }

    private fun interpret(param: Map<*, *>, dest: String, output:String, batch: String, serial: String): Mono<String> = Mono.just(client.send(dest, om.writeValueAsBytes(param))).flatMap {
        println("destination = $dest")
        println("bash /storm/Analysis/HRD/OncoHRD/Script/interpretation/interpret_HRD.sh /storm/Analysis/HRD/OncoHRD/$batch $serial")
        Mono.just(client.exec("bash /storm/Analysis/HRD/OncoHRD/Script/interpretation/interpret_HRD.sh /storm/Analysis/HRD/OncoHRD/$batch $serial"))
    }.flatMap {
        Mono.just(client.exec("cat $output"))
    }
    override fun negative(sample: Long, service: String): Mono<*> {
        TODO("Not yet implemented")
    }
}
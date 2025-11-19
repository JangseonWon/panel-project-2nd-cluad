package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.entity.Analysis
import com.gcgenome.lims.interpretable.Interpretable
import com.gcgenome.lims.ssh.SshClient
import com.gcgenome.lims.test.BloodCancerPanel
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.extra.math.max

@Component
class BloodCancer(val om: ObjectMapper, val repo: AnalysisRepository): Interpretable {
    private val client =  SshClient("172.19.207.52", 22, "root", "GCGenome12!", 9999, 1018)
    override fun chk(sample: Long, service: String): Boolean = BloodCancerPanel.values().stream().map{ t->t.code() }.anyMatch(service::equals)
    override fun interpret(sample: Long, service: String, param: Map<*, *>): Mono<*> =
        repo.findAnalysis(sample, service).max(Comparator.comparing(Analysis::createAt)).flatMap {analysis ->
            val batch = analysis.batch
            val panel = analysis.panel
            val serial = analysis.serial
            val dest = "/storm/Analysis/Somatic/HEMA_v2/results/${batch}/${panel}/output/interpretation/${serial}_Tier_annot.json"
            val output = "/storm/Analysis/Somatic/HEMA_v2/results/${batch}/${panel}/output/interpretation/${serial}_interpret.txt"
            interpret(param, dest, output)
        }
    private fun interpret(param: Map<*, *>, dest: String, output: String): Mono<String> = Mono.just(client.send(dest, om.writeValueAsBytes(param))).flatMap {
        println("/storm/User/sbkweon/pipeline/hema-malig/hema-malig_v2/scripts/interpret/01.run_interpret.sh $dest $output")
        Mono.just(client.exec("/storm/User/sbkweon/pipeline/hema-malig/hema-malig_v2/scripts/interpret/01.run_interpret.sh $dest $output"))
    }.flatMap {
        Mono.just(client.exec("cat $output"))
    }
    override fun negative(sample: Long, service: String): Mono<*> {
        TODO("Not yet implemented")
    }

}
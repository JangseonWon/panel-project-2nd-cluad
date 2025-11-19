package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.constants.Clazz
import com.gcgenome.lims.inserts.Insert
import com.gcgenome.lims.interpretable.kokr.InterpretationHeaderPhraseSingleKoKr
import com.gcgenome.lims.test.HasReferralDefault
import com.gcgenome.lims.test.SingleGenePanel
import org.springframework.stereotype.Component

/*
 Benign, Likely Benign이 보고되는 케이스가 있는 것만 빼고 희귀질환과 동일 프로세스. 음성 소견이 조금 다름
 검사 유전자가 BRCA는 뺀다. BRCA는 Cancer처럼 Verbose 사용
 */
@Component
class Single(om: ObjectMapper, requestDao: RequestDao, snvRepo: SnvDao, inserts: List<Insert>, private val tests: List<SingleGenePanel> = SingleGenePanel.values())
    : AbstractPanel<SingleGenePanel>(om, requestDao, snvRepo, inserts, InterpretationHeaderPhraseSingleKoKr()) {
    override fun chk(sample: Long, service: String): Boolean {
        return SingleGenePanel.values().filter{ it.gene.contains("BRCA").not() }.map{ t->t.code()}.contains(service)
    }
    override fun test(service: String): SingleGenePanel = tests.first { service == it.code() }
    override fun referralDefault(service: String): HasReferralDefault? {
        val test = test(service)
        return if(test is HasReferralDefault) test else null
    }
    override fun reportable(clazz: Clazz): Boolean = when(clazz) {
        Clazz.PV        -> true
        Clazz.LPV       -> true
        Clazz.VUS       -> true
        Clazz.LBV       -> true
        Clazz.BV        -> true
        else            -> false
    }
}
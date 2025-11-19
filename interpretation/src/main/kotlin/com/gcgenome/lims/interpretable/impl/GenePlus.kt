package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.inserts.Insert
import com.gcgenome.lims.interpretable.kokr.InterpretationHeaderPhraseGenePlusKoKr
import com.gcgenome.lims.test.GenePlusPanel
import com.gcgenome.lims.test.HasReferralDefault
import org.springframework.stereotype.Component

/*
 희귀질환과 동일 프로세스. 음성 소견이 조금 다름
 검사 유전자가 BRCA는 뺀다. BRCA는 Cancer처럼 Verbose 사용
 */
@Component
class GenePlus(
    om: ObjectMapper,
    requestDao: RequestDao,
    snvRepo: SnvDao,
    inserts: List<Insert>
) : AbstractPanel<GenePlusPanel> (om, requestDao, snvRepo, inserts, InterpretationHeaderPhraseGenePlusKoKr()) {
    override fun chk(sample: Long, service: String): Boolean {
        if ("S096".equals(service, ignoreCase = true)) return false
        if ("S097".equals(service, ignoreCase = true)) return false
        return GenePlusPanel.values().map{t->t.code()}.any(service::equals)
    }
    override fun test(service: String): GenePlusPanel = GenePlusPanel.values().first { service == it.code() }
    override fun referralDefault(service: String): HasReferralDefault = test(service)
}
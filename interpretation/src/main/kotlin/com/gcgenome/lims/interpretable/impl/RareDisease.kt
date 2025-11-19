package com.gcgenome.lims.interpretable.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.gcgenome.lims.inserts.Insert
import com.gcgenome.lims.test.HasReferralDefault
import com.gcgenome.lims.test.RareDiseasePanel
import org.springframework.stereotype.Component

@Component
class RareDisease(om: ObjectMapper, requestDao: RequestDao, snvRepo: SnvDao, inserts: List<Insert>) : AbstractPanel<RareDiseasePanel>(om, requestDao, snvRepo, inserts) {
    override fun chk(sample: Long, service: String): Boolean {
        if ("ON001".equals(service, ignoreCase = true)) return false
        if ("ON040".equals(service, ignoreCase = true)) return false
        return RareDiseasePanel.values().map{t->t.code()}.any(service::equals)
    }
    override fun test(service: String): RareDiseasePanel = RareDiseasePanel.values().first { service == it.code() }
    override fun referralDefault(service: String): HasReferralDefault = test(service)
}
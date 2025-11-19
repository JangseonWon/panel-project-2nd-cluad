package com.gcgenome.lims.analysis

import org.springframework.scheduling.annotation.Scheduled

class ConsensualClassRepository (

) {
    companion object {
        private val CONSENSUAL_CLASSES: Map<String, String> = HashMap()
        private class Subscriber {
            @Scheduled(fixedDelay = 1000 * 60 * 60)
            fun updateCandidates() {
                /*println("Update Consensual..")
                pending = true
                val t: TransactionStatus = tx.getTransaction(TransactionDefinition.withDefaults())
                CONSENSUAL_CLASSES.clear()
                consensualClassDao.findLast().forEach { v ->
                    var clazz: String = v.classification()
                    clazz = com.greencross.lims.analysis.GcgenomeAnalysisCrawler.mapCls(clazz)
                    if (clazz != null) CONSENSUAL_CLASSES.put(v.snv(), clazz)
                }
                tx.rollback(t)
                pending = false
                println("Update Complete")*/
            }
            private fun mapCls(clazz: String): String? {
                return if ("Pathogenic".equals(clazz, ignoreCase = true))        "P"
                else if ("Likely Pathogenic".equals(clazz, ignoreCase = true))   "LP"
                else if ("VUS".equals(clazz, ignoreCase = true))                 "VUS"
                //else if("Likely Benign".equalsIgnoreCase(clazz))	             "LB";		// LB, B는 Assign 하지 않는다
                //else if("Benign".equalsIgnoreCase(clazz))			             "B";
                else null
            }
        }
    }
}
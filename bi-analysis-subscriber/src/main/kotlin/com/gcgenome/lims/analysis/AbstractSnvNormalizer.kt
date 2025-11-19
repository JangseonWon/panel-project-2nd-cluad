package com.gcgenome.lims.analysis

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

// Document에 뭔가 추가할 내용이 있으면 추가한다
interface AbstractSnvNormalizer {
    fun MutableMap<String, String?>.normalize(): MutableMap<String, String> = mutableMapOf(
        "organism" to "homo sapiens",
        "reference" to "hg19",
        "create_at" to LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")),
    ).also { map ->
        this.forEach { row ->
            val header = row.key.replace(" ", "_")
            normalize(header, row.value)?.let { map[header] = it }
        }
    }
    fun MutableMap<String, String>.appendClassOrder(): MutableMap<String, String> = this.apply {
        val clazz = this["class"]
        if (clazz != null) this["class_order"] = order(clazz).toString()
    }
    private fun normalize(key: String, value: String?): String? {
        if(value==null || value == ".") return null
        return when {
            "qual".contentEquals(key, true)     -> if ("None".contentEquals(value, true)) null else value
            else -> value
        }
    }
    private fun order(clazz: String?): Int {
        if(clazz == null) return 9999
        val clazz = clazz.uppercase(Locale.getDefault())
        when {
            clazz.startsWith("PVS1")        -> return 10
            clazz.startsWith("PVS1?")       -> return 15
            clazz.startsWith("PV")          -> return 20
            clazz.startsWith("(L)PV")       -> return 25
            clazz.startsWith("CLINVAR(L)PV")-> return 28
            clazz.startsWith("DM")          -> return 30
            clazz.startsWith("HGMD_DM")     -> return 32
            clazz.startsWith("DM?")         -> return 35
            clazz.startsWith("PM5")         -> return 40
            clazz.startsWith("VUS_PM")      -> return 48
            clazz.startsWith("NO_QC_PM")    -> return 49
            clazz.startsWith("MAF0-S")      -> return 50
            clazz.startsWith("MAF0")        -> return 51
            clazz.startsWith("VUS_MAF0")    -> return 52
            clazz.startsWith("VUS-S")       -> return 55
            clazz.startsWith("VUS")         -> return 60
            clazz.startsWith("PP5")         -> return 70
            clazz.startsWith("PP5?")        -> return 75
            clazz.startsWith("VUS?")        -> return 85
            clazz.startsWith("SPLICEAI")    -> return 90
            clazz.startsWith("SPLICEAI_HIGH_PRECISION") -> return 91
            clazz.startsWith("SPLICEAI_RECOMMENDED") -> return 92
            clazz.startsWith("SPLICEAI_HIGH_RECALL") -> return 93
            clazz.startsWith("HIGH_EFFECT")-> return 100
            else                                    -> return 9999
        }
    }
}
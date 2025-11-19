package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.constants.Clazz
import com.gcgenome.lims.dto.Disease
import com.gcgenome.lims.dto.InterpretationPanel
import java.util.*
import java.util.stream.Collectors

internal class ResultPhraseSimpleKoKr {
    fun result(variants: List<Pair<InterpretationPanel.Variant, List<Disease>>>): String {
        val pv = variants.stream().filter { it.first.clazz == Clazz.PV}.collect(Collectors.toList())
        val lpv = variants.stream().filter { it.first.clazz == Clazz.LPV}.collect(Collectors.toList())
        val vus = variants.stream().filter { it.first.clazz == Clazz.VUS}.collect(Collectors.toList())
        val result = StringBuilder()
        if(pv.size<=0 && lpv.size<=0 && vus.size<=0) result.append("질환과 관련된 변이는 발견되지 않았습니다.")
        else if (pv.size == 1 && lpv.size <= 0 && vus.size <= 0) result.append("PV가 발견되었습니다.")
        else if (pv.size <= 0 && lpv.size == 1 && vus.size <= 0) result.append("LPV가 발견되었습니다.")
        else if (pv.size <= 0 && lpv.size <= 0 && vus.size == 1) result.append("VUS가 발견되었습니다.")
        else {
            val comments = LinkedList<String>()
            if (pv.size > 0) comments.add(String.format("PV %d개", pv.size))
            if (lpv.size > 0) comments.add(String.format("LPV %d개", lpv.size))
            if (vus.size > 0) comments.add(String.format("VUS %d개", vus.size))
            result.append(java.lang.String.join("와 ", comments)).append("가 발견되었습니다.")
        }
        return result.toString()
    }
}
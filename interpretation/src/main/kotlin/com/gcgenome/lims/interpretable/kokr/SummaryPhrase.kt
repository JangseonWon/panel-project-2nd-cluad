package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.constants.Clazz
import com.gcgenome.lims.dto.InterpretationPanel
import java.util.*

internal class SummaryPhrase {
    val dominants = setOf("AD","XLD","XD","DD","XL","MI")
    val recessives = setOf("AR","XLR","XR","DR")
    fun summary(variants: List<InterpretationPanel.Variant>): String {
        try {
            if(findIfMultiplePathogensExist(variants)) return "POSITIVE"
            // PV/LPV이고 {HOM/HEM 이거나} {HET이더라도 dominant} 이면 PV
            if(variants.firstOrNull(::positivePredicate) != null) return "POSITIVE"

            // {VUS거나} {PV/LPV이고 {AR이 조금이라도 의심} <- 이 단계에서 항상 HET임} 되면 VUS
            // AD, AR 다 있고 Het PV면 AR 가능성이 있어서 inconclusive로 판정한다고 함
            if(variants.firstOrNull(::inconclusivePredicate) != null) return "INCONCLUSIVE"
        } catch (e : NullPointerException){
            throw RuntimeException("해당 변이 목록 중 Inheritance 없는 값이 존재함! $variants", e)
        }
        return "NEGATIVE"
    }

    private fun positivePredicate(variant : InterpretationPanel.Variant) : Boolean {
        return when(variant.zygosity?.uppercase()) {
            "HOM" -> (variant.clazz == Clazz.PV || variant.clazz == Clazz.LPV)
            "HEM" -> (variant.clazz == Clazz.PV || variant.clazz == Clazz.LPV)
            "HET" -> (variant.clazz == Clazz.PV || variant.clazz == Clazz.LPV) && variant.inheritance!!.uppercase(Locale.getDefault()).split(",").stream().map(String::trim).allMatch(dominants::contains)
            else  -> (variant.clazz == Clazz.PV || variant.clazz == Clazz.LPV) && variant.inheritance!!.uppercase(Locale.getDefault()).split(",").stream().map(String::trim).allMatch(dominants::contains)
        }
    }
    private fun inconclusivePredicate(variant : InterpretationPanel.Variant) : Boolean = variant.clazz == Clazz.VUS || ((variant.clazz == Clazz.PV || variant.clazz == Clazz.LPV) && variant.inheritance!!.uppercase(Locale.getDefault()).split(",").stream().map(String::trim).anyMatch(recessives::contains))
    private fun findIfMultiplePathogensExist(variants : List<InterpretationPanel.Variant>) : Boolean = variants.filter { it.clazz == Clazz.LPV || it.clazz == Clazz.PV }.groupBy { it.gene }.toList().firstOrNull{it.second.size >= 2} != null
}

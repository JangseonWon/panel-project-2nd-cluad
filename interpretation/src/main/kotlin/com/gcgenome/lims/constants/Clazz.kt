package com.gcgenome.lims.constants

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

enum class Clazz(val fullName : String = "", @JsonValue val simpleName : String = "") {
    PV("Pathogenic Variant", "PV"),
    LPV("Likely Pathogenic Variant","LPV"),
    VUS("Variant of Uncertain Significance", "VUS"),
    LBV("Likely Benign Variant", "LBV"),
    BV("Benign Variant", "BV"),
    Other;

    private val PVs = setOf<String>("P", "PV")
    private val LPVs = setOf<String>("LP", "LPV")
    private val VUSs = setOf<String>("VUS")
    private val LBVs = setOf<String>("LB", "LBV")
    private val BVs = setOf<String>("B", "BV")

    @JsonCreator
    fun normalize(clazz: String) : Clazz{
        val findClazz = clazz.trim().uppercase(Locale.getDefault())
        return when{
            PVs.contains(findClazz) -> PV
            LPVs.contains(findClazz) -> LPV
            VUSs.contains(findClazz) -> VUS
            LBVs.contains(findClazz) -> LBV
            BVs.contains(findClazz) -> BV
            else -> Other
        }
    }
    fun abbreviation(): String = "$simpleName, $fullName"

    override fun toString(): String {
        return simpleName
    }
}
package com.gcgenome.lims.interpretable

internal class VariantClassPhraseVerbose: VariantClassPhrase {
    override fun normalize(clazz: String): String {
        return when (clazz) {
            "P", "PV"       -> "Pathogenic Variant"
            "LP", "LPV"     -> "Likely Pathogenic Variant"
            "VUS"           -> "Variant of Uncertain Significance"
            "B", "BV"       -> "Benign Variant"
            "LB", "LBV"     -> "Likely Benign Variant"
            else            -> "<?>"
        }
    }
}
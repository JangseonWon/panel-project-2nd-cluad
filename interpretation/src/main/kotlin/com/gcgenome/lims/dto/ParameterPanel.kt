package com.gcgenome.lims.dto

data class ParameterPanel(
    val sex: Sex,
    val previous: InterpretationPanel,
    val disease: Map<String, List<Disease>>,
    val suffix: String?
) {
    enum class Sex{
        F, M
    }
}



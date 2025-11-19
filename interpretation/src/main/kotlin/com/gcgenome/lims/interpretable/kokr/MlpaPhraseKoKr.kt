package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.dto.InterpretationPanelMlpa

internal class MlpaPhraseKoKr {
    fun result(mlpa: InterpretationPanelMlpa.Mlpa): String {
        val resultMsg = when (mlpa.result) {
            "Not Detected" -> "deletion/duplication 소견이 관찰되지 않았습니다."
            else -> "${mlpa.exons}번의 ${mlpa.zygosity} ${mlpa.delDup}이 관찰되었습니다."
        }
        return "${mlpa.gene} 유전자의 exon deletion/duplication을 확인하기 위해 MLPA 검사를 시행한 결과, $resultMsg"
    }
}
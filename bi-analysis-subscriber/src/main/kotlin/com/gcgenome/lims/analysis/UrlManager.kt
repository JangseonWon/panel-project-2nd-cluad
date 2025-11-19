package com.gcgenome.lims.analysis

import com.gcgenome.lims.analysis.entity.Analysis
import com.gcgenome.lims.analysis.entity.Request

interface UrlManager {
    fun toUrl(request: Request, analysis: Analysis): String?
}
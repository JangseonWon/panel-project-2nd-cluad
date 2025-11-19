package com.gcgenome.lims.analysis

import com.gcgenome.lims.analysis.entity.Analysis
import com.gcgenome.lims.analysis.entity.Request
import org.springframework.stereotype.Service

@Service
class SampleUrlManager: UrlManager {
    override fun toUrl(request: Request, analysis: Analysis) = "http://172.19.208.100/sample.html#${request.sample}"
}

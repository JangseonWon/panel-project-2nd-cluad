package com.gcgenome.lims.interpretable.impl

import com.gcgenome.lims.entity.Interpretation
import com.gcgenome.lims.entity.Request
import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface RequestRepository: QuerydslR2dbcRepository<Request, Interpretation.Companion.InterpretationPK>
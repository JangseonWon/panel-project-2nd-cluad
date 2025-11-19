package com.gcgenome.lims.service

import com.gcgenome.lims.entity.Interpretation
import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface Repository: QuerydslR2dbcRepository<Interpretation, Interpretation.Companion.InterpretationPK>
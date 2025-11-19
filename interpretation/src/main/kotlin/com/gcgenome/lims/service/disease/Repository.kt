package com.gcgenome.lims.service.disease

import com.gcgenome.lims.entity.DiseasePredefined
import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.springframework.stereotype.Repository

@Repository("DiseaseRepository")
interface Repository: QuerydslR2dbcRepository<DiseasePredefined, String>
package com.gcgenome.lims.repo

import com.gcgenome.lims.entity.SnvConsensualClass
import com.gcgenome.querydsl.PersistQuerydslR2dbcRepo

interface SnvConsensualClassRepo : PersistQuerydslR2dbcRepo<SnvConsensualClass, SnvConsensualClass.Companion.SnvConsensualClassPk> {}
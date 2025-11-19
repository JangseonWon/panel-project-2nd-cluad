package com.gcgenome.lims.repo

import com.gcgenome.lims.entity.SnvToUpdate
import com.gcgenome.querydsl.PersistQuerydslR2dbcRepo

interface SnvToUpdateRepo : PersistQuerydslR2dbcRepo<SnvToUpdate, SnvToUpdate.Companion.SnvToUpdatePk> {}
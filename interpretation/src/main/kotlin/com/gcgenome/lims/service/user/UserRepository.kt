package com.gcgenome.lims.service.user

import com.gcgenome.lims.entity.User
import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: QuerydslR2dbcRepository<User, String>
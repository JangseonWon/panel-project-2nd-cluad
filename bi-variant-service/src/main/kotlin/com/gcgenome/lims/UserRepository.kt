package com.gcgenome.lims

import com.gcgenome.lims.entity.User
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: R2dbcRepository<User, String>
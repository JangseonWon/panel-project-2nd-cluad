package com.gcgenome.lims.service.user

import com.gcgenome.lims.entity.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: ReactiveCrudRepository<User, String>
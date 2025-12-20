package com.evandhardspace.chatapp.infra.repository

import com.evandhardspace.chatapp.domain.model.UserId
import com.evandhardspace.chatapp.infra.database.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<UserEntity, UserId> {
    fun findByEmail(email: String): UserEntity?
    fun findByEmailOrUsername(email: String, username: String): UserEntity?

}
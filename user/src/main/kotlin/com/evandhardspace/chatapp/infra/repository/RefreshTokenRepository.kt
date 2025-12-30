package com.evandhardspace.chatapp.infra.repository


import com.evandhardspace.chatapp.domain.type.UserId
import com.evandhardspace.chatapp.infra.database.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun deleteByHashedToken(hashedToken: String)
    fun deleteByUserId(userId: UserId)
    fun findByHashedToken(hashedToken: String): RefreshTokenEntity?
}
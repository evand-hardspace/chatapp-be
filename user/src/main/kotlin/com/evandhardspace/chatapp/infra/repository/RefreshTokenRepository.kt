package com.evandhardspace.chatapp.infra.repository


import com.evandhardspace.chatapp.domain.model.UserId
import com.evandhardspace.chatapp.infra.database.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository: JpaRepository<RefreshTokenEntity, Long> {
    fun findByUserIdAndHashedToken(userId: UserId, hashedToken: String): RefreshTokenEntity?
    fun deleteByUserIdAndHashedToken(userId: UserId, hashedToken: String)
    fun deleteByUserId(userId: UserId)
}
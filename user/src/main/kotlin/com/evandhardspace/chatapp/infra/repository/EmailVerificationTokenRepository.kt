package com.evandhardspace.chatapp.infra.repository

import com.evandhardspace.chatapp.infra.database.entity.EmailVerificationTokenEntity
import com.evandhardspace.chatapp.infra.database.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface EmailVerificationTokenRepository: JpaRepository<EmailVerificationTokenEntity, Long> {
    fun findByToken(token: String): EmailVerificationTokenEntity?
    fun deleteByExpiresAtLessThan(now: Instant)
    fun findByUsedAtIsNull(user: UserEntity): List<EmailVerificationTokenEntity>
}
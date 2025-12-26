package com.evandhardspace.chatapp.service.auth

import com.evandhardspace.chatapp.domain.exception.InvalidTokenException
import com.evandhardspace.chatapp.domain.exception.UserNotFoundException
import com.evandhardspace.chatapp.domain.model.EmailVerificationToken
import com.evandhardspace.chatapp.infra.database.entity.EmailVerificationTokenEntity
import com.evandhardspace.chatapp.infra.database.entity.isExpired
import com.evandhardspace.chatapp.infra.database.entity.isUsed
import com.evandhardspace.chatapp.infra.database.mapper.toEmailVerificationToken
import com.evandhardspace.chatapp.infra.repository.EmailVerificationTokenRepository
import com.evandhardspace.chatapp.infra.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import kotlin.time.Duration.Companion.hours


@Service
class EmailVerificationService(
    private val emailVerificationTokenRepository: EmailVerificationTokenRepository,
    private val userRepository: UserRepository,
    @param:Value($$"${app.email.verification.expiration-hours}")
    private val expirationHours: Long,
) {
    @Transactional
    fun createVerificationToken(email: String): EmailVerificationToken {
        val userEntity = userRepository.findByEmail(email) ?: throw UserNotFoundException()
        emailVerificationTokenRepository.invalidateActiveTokensForUser(userEntity)

        val token = EmailVerificationTokenEntity(
            expiresAt = Instant.now().plusSeconds(expirationHours.hours.inWholeSeconds),
            user = userEntity,
        )

        return emailVerificationTokenRepository
            .save(token)
            .toEmailVerificationToken()
    }

    @Transactional
    fun verifyEmail(token: String) {
        val verificationToken = emailVerificationTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Invalid email verification token.")
        val now = Instant.now()

        if(verificationToken.isUsed) throw InvalidTokenException("Email verification token is already used.")
        if(verificationToken.isExpired(now)) throw InvalidTokenException("Email verification token has expired.")

        emailVerificationTokenRepository.save(
            verificationToken.apply { usedAt = now }
        )

        userRepository.save(verificationToken.user.apply { hasVerifiedEmail = true })
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanUpExpiredTokens() {
        emailVerificationTokenRepository.deleteByExpiresAtLessThan(Instant.now())
    }
}
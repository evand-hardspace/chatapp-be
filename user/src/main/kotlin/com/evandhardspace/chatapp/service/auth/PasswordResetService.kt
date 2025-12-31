package com.evandhardspace.chatapp.service.auth

import com.evandhardspace.chatapp.domain.events.ChatAppEvent
import com.evandhardspace.chatapp.domain.events.user.UserEvent
import com.evandhardspace.chatapp.domain.exception.InvalidCredentialsException
import com.evandhardspace.chatapp.domain.exception.InvalidTokenException
import com.evandhardspace.chatapp.domain.exception.SamePasswordException
import com.evandhardspace.chatapp.domain.exception.UserNotFoundException
import com.evandhardspace.chatapp.domain.type.UserId
import com.evandhardspace.chatapp.infra.database.entity.PasswordResetTokenEntity
import com.evandhardspace.chatapp.infra.database.entity.isExpired
import com.evandhardspace.chatapp.infra.database.entity.isUsed
import com.evandhardspace.chatapp.infra.messagequeue.EventPublisher
import com.evandhardspace.chatapp.infra.messagequeue.publishWith
import com.evandhardspace.chatapp.infra.repository.PasswordResetTokenRepository
import com.evandhardspace.chatapp.infra.repository.RefreshTokenRepository
import com.evandhardspace.chatapp.infra.repository.UserRepository
import com.evandhardspace.chatapp.infra.security.PasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.evandhardspace.chatapp.util.requireNotNull
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

@Service
class PasswordResetService(
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    @param:Value($$"${chatapp.reset-password.expiration-minutes}")
    private val expirationMinutes: Long,
    private val eventPublisher: EventPublisher,
) {

    @Transactional
    fun requestPasswordReset(email: String) {
        val user = userRepository.findByEmail(email) ?: return

        passwordResetTokenRepository.invalidateActiveTokensForUser(user)

        val token = PasswordResetTokenEntity(
            user = user,
            expiresAt = Instant.now().plusSeconds(expirationMinutes.minutes.inWholeSeconds),
        )

        passwordResetTokenRepository.save(token)

        UserEvent.RequestResetPassword(
            userId = user.id.requireNotNull(),
            email = user.email,
            username = user.username,
            passwordResetToken = token.token,
            expiresInMinutes = expirationMinutes,
        ).publishWith(eventPublisher)
    }


    @Transactional
    fun resetPassword(token: String, newPassword: String) {
        val resetToken = passwordResetTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Invalid password reset token.")
        val now = Instant.now()
        if(resetToken.isUsed) throw InvalidTokenException("Email verification token is already used.")
        if(resetToken.isExpired(now)) throw InvalidTokenException("Email verification token has expired.")

        passwordResetTokenRepository.save(
            resetToken.apply { usedAt = now }
        )

        val user = resetToken.user

        if(passwordEncoder.matches(newPassword, user.hashedPassword)) {
            throw SamePasswordException()
        }

        val hashedNewPassword = passwordEncoder.encode(newPassword)
        userRepository.save(
            user.apply { hashedPassword = hashedNewPassword }
        )

        passwordResetTokenRepository.save(
            resetToken.apply { usedAt = now }
        )

        refreshTokenRepository.deleteByUserId(user.id.requireNotNull())
    }

    @Transactional
    fun changePassword(userId: UserId, oldPassword: String, newPassword: String) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        if(!passwordEncoder.matches(oldPassword, user.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        if(oldPassword == newPassword) {
            throw SamePasswordException()
        }

        refreshTokenRepository.deleteByUserId(user.id.requireNotNull())

        val hashedNewPassword = passwordEncoder.encode(newPassword)
        userRepository.save(
            user.apply { hashedPassword = hashedNewPassword }
        )
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanUpExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiresAtLessThan(Instant.now())
    }
}

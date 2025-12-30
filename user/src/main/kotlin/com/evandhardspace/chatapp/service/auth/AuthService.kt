package com.evandhardspace.chatapp.service.auth

import com.evandhardspace.chatapp.domain.events.user.UserEvent
import com.evandhardspace.chatapp.domain.exception.EmailNotVerifiedException
import com.evandhardspace.chatapp.domain.exception.InvalidCredentialsException
import com.evandhardspace.chatapp.domain.exception.InvalidTokenException
import com.evandhardspace.chatapp.domain.exception.UserAlreadyExistsException
import com.evandhardspace.chatapp.domain.exception.UserNotFoundException
import com.evandhardspace.chatapp.domain.model.AuthenticatedUser
import com.evandhardspace.chatapp.domain.model.Token
import com.evandhardspace.chatapp.domain.model.User
import com.evandhardspace.chatapp.domain.type.UserId
import com.evandhardspace.chatapp.infra.database.entity.RefreshTokenEntity
import com.evandhardspace.chatapp.infra.database.entity.UserEntity
import com.evandhardspace.chatapp.infra.database.mapper.toUser
import com.evandhardspace.chatapp.infra.messagequeue.EventPublisher
import com.evandhardspace.chatapp.infra.messagequeue.publishWith
import com.evandhardspace.chatapp.infra.repository.RefreshTokenRepository
import com.evandhardspace.chatapp.infra.repository.UserRepository
import com.evandhardspace.chatapp.infra.security.PasswordEncoder
import com.evandhardspace.chatapp.infra.security.RefreshTokenHasher
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authTokenService: AuthTokenService,
    private val refreshTokenHasher: RefreshTokenHasher,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val emailVerificationService: EmailVerificationService,
    private val eventPublisher: EventPublisher,
) {
    @Transactional
    fun register(email: String, username: String, password: String): User {
        val email = email.trim()
        userRepository.findByEmailOrUsername(
            email = email,
            username = username,
        )?.run { throw UserAlreadyExistsException() }

        return userRepository.saveAndFlush(
            UserEntity(
                email = email,
                username = username,
                hasVerifiedEmail = false,
                hashedPassword = passwordEncoder.encode(password),
            )
        ).toUser()
            .also { savedUser ->
                val token = emailVerificationService.createVerificationToken(email)
                UserEvent.Created(
                    userId = savedUser.id,
                    email = savedUser.email,
                    username = savedUser.username,
                    verificationToken = token.token,
                ).publishWith(eventPublisher)
            }
    }

    fun login(
        email: String,
        password: String,
    ): AuthenticatedUser {
        val userEntity = userRepository.findByEmail(email.trim())
            ?: throw InvalidCredentialsException()

        if (!passwordEncoder.matches(password, userEntity.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        if (userEntity.hasVerifiedEmail.not()) throw EmailNotVerifiedException()

        return userEntity.id?.let { userId ->
            val accessToken = authTokenService.generateAccessToken(userId)
            val refreshToken = storeRefreshToken(userId) { authTokenService.generateRefreshToken() }

            AuthenticatedUser(
                user = userEntity.toUser(),
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional
    fun refresh(
        refreshToken: String,
    ): AuthenticatedUser {
        val hashedToken = Token.RefreshToken(refreshToken).hashed()

        val refreshTokenEntity = refreshTokenRepository.findByHashedToken(hashedToken)
            ?: throw InvalidTokenException("Invalid refresh token.")

        val now = Instant.now()

        if (refreshTokenEntity.expiresAt.isBefore(now)) {
            refreshTokenRepository.deleteByHashedToken(hashedToken)
            throw InvalidTokenException("Refresh token expired.")
        }

        val userId = refreshTokenEntity.userId
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

        refreshTokenRepository.deleteByHashedToken(hashedToken)

        val newAccessToken = authTokenService.generateAccessToken(userId)
        val newRefreshToken = storeRefreshToken(userId) { authTokenService.generateRefreshToken() }

        return AuthenticatedUser(
            user = user.toUser(),
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
        )
    }

    @Transactional
    fun logout(refreshToken: String) {
        val hashedToken = Token.RefreshToken(refreshToken).hashed()
        refreshTokenRepository.deleteByHashedToken(hashedToken)
    }

    private fun storeRefreshToken(
        userId: UserId,
        generateToken: () -> Token.RefreshToken,
    ): Token.RefreshToken {
        val expiryMs = authTokenService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        repeat(3) {
            val token = generateToken()
            try {
                refreshTokenRepository.save(
                    RefreshTokenEntity(
                        userId = userId,
                        expiresAt = expiresAt,
                        hashedToken = token.hashed(),
                    )
                )
                return token
            } catch (_: DataIntegrityViolationException) {
                // collision, continue
            }
        }
        error("Failed to generate unique refresh token.")
    }

    private fun Token.RefreshToken.hashed(): String =
        refreshTokenHasher.hash(this@hashed.value)
}

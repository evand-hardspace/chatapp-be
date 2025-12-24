package com.evandhardspace.chatapp.service.auth

import com.evandhardspace.chatapp.domain.exception.EmailNotVerifiedException
import com.evandhardspace.chatapp.domain.exception.InvalidCredentialsException
import com.evandhardspace.chatapp.domain.exception.InvalidTokenException
import com.evandhardspace.chatapp.domain.exception.UserAlreadyExistsException
import com.evandhardspace.chatapp.domain.exception.UserNotFoundException
import com.evandhardspace.chatapp.domain.model.AuthenticatedUser
import com.evandhardspace.chatapp.domain.model.Token
import com.evandhardspace.chatapp.domain.model.User
import com.evandhardspace.chatapp.domain.model.UserId
import com.evandhardspace.chatapp.infra.database.entity.RefreshTokenEntity
import com.evandhardspace.chatapp.infra.database.entity.UserEntity
import com.evandhardspace.chatapp.infra.database.mapper.toUser
import com.evandhardspace.chatapp.infra.repository.RefreshTokenRepository
import com.evandhardspace.chatapp.infra.repository.UserRepository
import com.evandhardspace.chatapp.infra.security.PasswordEncoder
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val emailVerificationService: EmailVerificationService,
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
            .also { emailVerificationService.createVerificationToken(email) }
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

        if(userEntity.hasVerifiedEmail.not()) throw EmailNotVerifiedException()

        return userEntity.id?.let { userId ->
            val (accessToken, refreshToken) = jwtService.generateTokens(userId)

            storeRefreshToken(userId, refreshToken)

            AuthenticatedUser(
                user = userEntity.toUser(),
                accessToken = accessToken.value,
                refreshToken = refreshToken.value,
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional
    fun refresh(
        refreshToken: String,
    ): AuthenticatedUser {
        val token = Token.RefreshToken(refreshToken)
        if (jwtService.isValidToken(token).not()) throw InvalidTokenException("Invalid refresh token.")

        val userId = jwtService.getUserId(token)
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        val hashedToken = hashToken(token)

        return user.id?.let { userId ->
            refreshTokenRepository.findByUserIdAndHashedToken(userId, hashedToken)
                ?: throw InvalidTokenException("Invalid refresh token.")

            refreshTokenRepository.deleteByUserIdAndHashedToken(userId, hashedToken)

            val (newAccessToken, newRefreshToken) = jwtService.generateTokens(userId)

            storeRefreshToken(userId, newRefreshToken)
            AuthenticatedUser(
                user = user.toUser(),
                accessToken = newAccessToken.value,
                refreshToken = newRefreshToken.value,
            )
        } ?: throw UserNotFoundException()
    }

    @Transactional
    fun logout(refreshToken: String) {
        val token = Token.RefreshToken(refreshToken)
        val userId = jwtService.getUserId(token)
        val hashedToken = hashToken(token)
        refreshTokenRepository.deleteByUserIdAndHashedToken(userId, hashedToken)
    }

    private fun storeRefreshToken(userId: UserId, token: Token.RefreshToken) {
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashToken(token),
            )
        )

    }

    private fun hashToken(token: Token.RefreshToken): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = digest.digest(token.value.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashedBytes)
    }
}

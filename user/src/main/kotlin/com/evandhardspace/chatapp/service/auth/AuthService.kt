package com.evandhardspace.chatapp.service.auth

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
import util.requireNotNull
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun register(email: String, username: String, password: String): User {
        userRepository.findByEmailOrUsername(
            email = email.trim(),
            username = username.trim(),
        )?.run { throw UserAlreadyExistsException() }

        return userRepository.save(
            UserEntity(
                email = email.trim(),
                username = username.trim(),
                hasVerifiedEmail = false,
                hashedPassword = passwordEncoder.encode(password),
            )
        ).toUser()
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

        // TODO: Check for verified email

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
        if(jwtService.validateToken(token).not()) throw InvalidTokenException("Invalid refresh token.")

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

    private fun hashToken(token: Token.RefreshToken): String{
            val digest = MessageDigest.getInstance("SHA-256")
            val hashedBytes = digest.digest(token.value.encodeToByteArray())
            return Base64.getEncoder().encodeToString(hashedBytes)
        }
}

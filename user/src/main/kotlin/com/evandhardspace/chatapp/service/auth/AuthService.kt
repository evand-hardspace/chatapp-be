package com.evandhardspace.chatapp.service.auth

import com.evandhardspace.chatapp.domain.exception.InvalidCredentialsException
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
import org.springframework.stereotype.Service
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

    private fun storeRefreshToken(userId: UserId, token: Token.RefreshToken) {
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = token.hashed,
            )
        )

    }

    private val Token.RefreshToken.hashed: String
        get() {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashedBytes = digest.digest(this@hashed.value.encodeToByteArray())
            return Base64.getEncoder().encodeToString(hashedBytes)
        }
}

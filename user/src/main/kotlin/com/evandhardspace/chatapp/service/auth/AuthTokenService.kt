package com.evandhardspace.chatapp.service.auth

import com.evandhardspace.chatapp.domain.exception.InvalidTokenException
import com.evandhardspace.chatapp.domain.model.Token
import com.evandhardspace.chatapp.domain.model.TokenType
import com.evandhardspace.chatapp.domain.type.UserId
import com.evandhardspace.chatapp.infra.security.TokenGenerator
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID
import kotlin.contracts.contract
import kotlin.io.encoding.Base64
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@Service
class AuthTokenService(
    @param:Value($$"${jwt.secret}") private val secretBase64: String,
    @param:Value($$"${jwt.expiration-minutes}") private val expirationMinutes: Int,
    private val tokenGenerator: TokenGenerator,
) {
    private val secretKey = Keys.hmacShaKeyFor(
        Base64.decode(secretBase64)
    )
    private val accessTokenValidityMs = expirationMinutes.minutes.inWholeMilliseconds
    val refreshTokenValidityMs = 30.days.inWholeMilliseconds

    fun generateAccessToken(
        userId: UserId,
    ): Token.AccessToken = Date().let { now ->
        Jwts.builder()
            .subject(userId.toString())
            .claim(TypeKey, TokenType.Access.value)
            .issuedAt(now)
            .expiration(
                Date(now.time + accessTokenValidityMs)
            )
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
            .run(Token::AccessToken)
    }

    fun generateRefreshToken(): Token.RefreshToken =
        tokenGenerator.generateSecureToken().run(Token::RefreshToken)

    fun getUserId(token: Token.AccessToken): UserId {
        val claims = parseAllClaims(token.value) ?: throw InvalidTokenException(
            "The attached JWT token is not valid."
        )

        return UUID.fromString(claims.subject)
    }

    fun isValidToken(token: Token.AccessToken): Boolean {
        val claims = parseAllClaims(token.value) ?: return false
        val tokenType = TokenType.fromValue(
            claims[TypeKey] as? String ?: return false
        )

        return tokenType == TokenType.Access
    }

    private fun parseAllClaims(token: String): Claims? = runCatching {
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token.rawToken)
            .payload
    }.getOrNull()
}

fun String?.isBearer(): Boolean {
    contract { returns(true) implies (this@isBearer != null) }
    if (this == null) return false
    return startsWith(BearerPrefix)
}

private val String.rawToken: String
    get() = if (startsWith(BearerPrefix)) removePrefix(BearerPrefix) else this

private const val TypeKey = "type"
private const val BearerPrefix = "Bearer "
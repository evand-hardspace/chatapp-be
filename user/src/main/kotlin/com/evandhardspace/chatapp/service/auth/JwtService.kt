package com.evandhardspace.chatapp.service.auth

import com.evandhardspace.chatapp.domain.exception.InvalidTokenException
import com.evandhardspace.chatapp.domain.model.Token
import com.evandhardspace.chatapp.domain.model.TokenType
import com.evandhardspace.chatapp.domain.model.UserId
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
class JwtService(
    @param:Value($$"${jwt.secret}") private val secretBase64: String,
    @param:Value($$"${jwt.expiration-minutes}") private val expirationMinutes: Int,
) {
    private val secretKey = Keys.hmacShaKeyFor(
        Base64.decode(secretBase64)
    )
    private val accessTokenValidityMs = expirationMinutes.minutes.inWholeMilliseconds
    val refreshTokenValidityMs = 30.days.inWholeMilliseconds

    // FIXME(1): migrate refresh token to nonsense
    fun generateTokens(userId: UUID): Pair<Token.AccessToken, Token.RefreshToken> =
        generateToken(userId, TokenType.Access).run(Token::AccessToken) to
                generateToken(userId, TokenType.Refresh).run(Token::RefreshToken)

    fun generateToken(
        userId: UserId,
        type: TokenType,
    ): String {
        val now = Date()
        val expiry = when (type) {
            TokenType.Access -> accessTokenValidityMs
            TokenType.Refresh -> refreshTokenValidityMs
        }
        val expiryDate = Date(now.time + expiry)

        return Jwts.builder()
            .subject(userId.toString())
            .claim(TypeKey, type.value)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()

    }

    fun getUserId(token: Token): UserId {
        val claims = parseAllClaims(token.value) ?: throw InvalidTokenException(
            "The attached JWT token is not valid."
        )

        return UUID.fromString(claims.subject)
    }

    fun isValidToken(token: Token): Boolean {
        val claims = parseAllClaims(token.value) ?: return false
        val tokenType = TokenType.fromValue(
            claims[TypeKey] as? String ?: return false
        )

        return when (token) {
            is Token.AccessToken -> tokenType == TokenType.Access
            is Token.RefreshToken -> tokenType == TokenType.Refresh
        }
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
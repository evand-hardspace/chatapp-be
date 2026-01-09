package com.evandhardspace.chatapp.domain.token

enum class TokenType(val value: String) {
    Access("access"),
    Refresh("refresh");

    companion object {
        fun fromValue(value: String): TokenType? = entries.find { it.value == value }
    }
}

sealed interface Token {

    val value: String
    @JvmInline
    value class AccessToken(override val value: String): Token

    @JvmInline
    value class RefreshToken(override val value: String): Token
}
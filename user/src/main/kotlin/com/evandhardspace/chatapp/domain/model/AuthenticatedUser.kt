package com.evandhardspace.chatapp.domain.model

data class AuthenticatedUser(
    val user: User,
    val accessToken: Token.AccessToken,
    val refreshToken: Token.RefreshToken,
)

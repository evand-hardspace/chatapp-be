package com.evandhardspace.chatapp.domain.model

import com.evandhardspace.chatapp.domain.token.Token

data class AuthenticatedUser(
    val user: User,
    val accessToken: Token.AccessToken,
    val refreshToken: Token.RefreshToken,
)

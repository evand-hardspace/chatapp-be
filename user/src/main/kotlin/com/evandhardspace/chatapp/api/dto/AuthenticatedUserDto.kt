package com.evandhardspace.chatapp.api.dto


data class AuthenticatedUserDto(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String,
)

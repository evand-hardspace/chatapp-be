package com.evandhardspace.chatapp.api.mapper

import com.evandhardspace.chatapp.api.dto.AuthenticatedUserDto
import com.evandhardspace.chatapp.api.dto.UserDto
import com.evandhardspace.chatapp.domain.model.AuthenticatedUser
import com.evandhardspace.chatapp.domain.model.User

fun AuthenticatedUser.toAuthenticatedUserDto(): AuthenticatedUserDto {
    return AuthenticatedUserDto(
        user = user.toUserDto(),
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}

fun User.toUserDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasEmailVerified,
    )
}
package com.evandhardspace.chatapp.api.dto

import com.evandhardspace.chatapp.domain.model.UserId

data class UserDto(
    val id: UserId,
    val email: String,
    val username: String,
    val hasVerifiedEmail: Boolean,
)
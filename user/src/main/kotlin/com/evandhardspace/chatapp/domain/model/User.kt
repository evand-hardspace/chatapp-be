package com.evandhardspace.chatapp.domain.model

import com.evandhardspace.chatapp.domain.type.UserId

data class User(
    val id: UserId,
    val username: String,
    val email: String,
    val hasEmailVerified: Boolean,
)

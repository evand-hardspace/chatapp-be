package com.evandhardspace.chatapp.infra.database.mapper

import com.evandhardspace.chatapp.domain.model.User
import com.evandhardspace.chatapp.infra.database.entity.UserEntity
import com.evandhardspace.chatapp.util.requireNotNull

fun UserEntity.toUser() = User(
    id = id.requireNotNull { "User id cannot be null" },
    username = username,
    email = email,
    hasEmailVerified = hasVerifiedEmail,
)

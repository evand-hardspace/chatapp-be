package com.evandhardspace.chatapp.domain.model

import com.evandhardspace.chatapp.domain.type.UserId

data class ChatParticipant(
    val userId: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String?,
)

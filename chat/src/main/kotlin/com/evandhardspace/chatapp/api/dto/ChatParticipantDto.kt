package com.evandhardspace.chatapp.api.dto

import com.evandhardspace.chatapp.domain.type.UserId

data class ChatParticipantDto(
    val userId: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String?,
)

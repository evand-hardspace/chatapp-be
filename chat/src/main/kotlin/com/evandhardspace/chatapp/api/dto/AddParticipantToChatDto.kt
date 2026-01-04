package com.evandhardspace.chatapp.api.dto

import com.evandhardspace.chatapp.api.util.ChatParticipantSize
import com.evandhardspace.chatapp.domain.type.UserId

data class AddParticipantToChatDto(
    @field:ChatParticipantSize
    val userIds: List<UserId>,
)

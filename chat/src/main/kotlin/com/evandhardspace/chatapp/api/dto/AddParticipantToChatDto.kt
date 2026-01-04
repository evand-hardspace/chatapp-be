package com.evandhardspace.chatapp.api.dto

import com.evandhardspace.chatapp.domain.type.UserId
import jakarta.validation.constraints.Size

data class AddParticipantToChatDto(
    @field:Size(
        min = 1,
        max = 50,
    )
    val userIds: List<UserId>,
)

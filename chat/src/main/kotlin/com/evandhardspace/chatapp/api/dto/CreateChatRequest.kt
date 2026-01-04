package com.evandhardspace.chatapp.api.dto

import com.evandhardspace.chatapp.domain.type.UserId
import jakarta.validation.constraints.Size

data class CreateChatRequest(
    @field:Size(
        min = 1,
        max = 50,
        message = "Chats must have at least 2 unique participants, but not more than 50."
    )
    val otherUserIds: List<UserId>
)

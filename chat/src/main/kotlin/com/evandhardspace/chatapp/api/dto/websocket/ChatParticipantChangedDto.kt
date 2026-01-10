package com.evandhardspace.chatapp.api.dto.websocket

import com.evandhardspace.chatapp.domain.type.ChatId

data class ChatParticipantsChangedDto(
    val chatId: ChatId,
)

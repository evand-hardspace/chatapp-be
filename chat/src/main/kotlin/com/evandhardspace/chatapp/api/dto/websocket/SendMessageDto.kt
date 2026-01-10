package com.evandhardspace.chatapp.api.dto.websocket

import com.evandhardspace.chatapp.domain.type.ChatId
import com.evandhardspace.chatapp.domain.type.ChatMessageId


data class SendMessageDto(
    val chatId: ChatId,
    val content: String,
    val messageId: ChatMessageId? = null,
)
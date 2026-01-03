package com.evandhardspace.chatapp.domain.model

import com.evandhardspace.chatapp.domain.type.ChatId
import com.evandhardspace.chatapp.domain.type.ChatMessageId
import java.time.Instant

data class ChatMessage(
    val id: ChatMessageId,
    val chatId: ChatId,
    val sender: ChatParticipant,
    val content: String,
    val createdAt: Instant,
)
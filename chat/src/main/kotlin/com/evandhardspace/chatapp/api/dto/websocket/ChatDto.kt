package com.evandhardspace.chatapp.api.dto.websocket

import com.evandhardspace.chatapp.api.dto.ChatMessageDto
import com.evandhardspace.chatapp.api.dto.ChatParticipantDto
import com.evandhardspace.chatapp.domain.type.ChatId
import java.time.Instant

data class ChatDto(
    val id: ChatId,
    val participants: List<ChatParticipantDto>,
    val lastActivityAt: Instant,
    val lastMessage: ChatMessageDto?,
    val creator: ChatParticipantDto,
)
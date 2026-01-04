package com.evandhardspace.chatapp.api.mapper

import com.evandhardspace.chatapp.api.dto.ChatDto
import com.evandhardspace.chatapp.api.dto.ChatMessageDto
import com.evandhardspace.chatapp.api.dto.ChatParticipantDto
import com.evandhardspace.chatapp.domain.model.Chat
import com.evandhardspace.chatapp.domain.model.ChatMessage
import com.evandhardspace.chatapp.domain.model.ChatParticipant

fun Chat.toChatDto(): ChatDto = ChatDto(
    id = id,
    participants = participants.map(ChatParticipant::toChatParticipantDto),
    lastActivityAt = lastActivityAt,
    lastMessage = lastMessage?.toChatMessageDto(),
    creator = creator.toChatParticipantDto(),
)

fun ChatMessage.toChatMessageDto(): ChatMessageDto =
    ChatMessageDto(
        id = id,
        chatId = chatId,
        content = content,
        createdAt = createdAt,
        senderId = sender.userId,
    )

fun ChatParticipant.toChatParticipantDto(): ChatParticipantDto =
    ChatParticipantDto(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl,
    )
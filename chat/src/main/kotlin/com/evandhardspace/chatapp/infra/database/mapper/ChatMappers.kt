package com.evandhardspace.chatapp.infra.database.mapper

import com.evandhardspace.chatapp.domain.model.Chat
import com.evandhardspace.chatapp.domain.model.ChatMessage
import com.evandhardspace.chatapp.domain.model.ChatParticipant
import com.evandhardspace.chatapp.infra.database.entity.ChatEntity
import com.evandhardspace.chatapp.infra.database.entity.ChatParticipantEntity
import com.evandhardspace.chatapp.infra.database.entity.idNotNull

fun ChatEntity.toChat(lastMessage: ChatMessage? = null): Chat =
    Chat(
        id = idNotNull,
        participants = participants.map(ChatParticipantEntity::toChatParticipant).toSet(),
        creator = creator.toChatParticipant(),
        lastActivityAt = lastMessage?.createdAt ?: createdAt,
        createdAt = createdAt,
        lastMessage = lastMessage,
    )

fun ChatParticipantEntity.toChatParticipant(): ChatParticipant =
    ChatParticipant(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl,
    )

fun ChatParticipant.toChatParticipantEntity(): ChatParticipantEntity =
    ChatParticipantEntity(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl,
    )

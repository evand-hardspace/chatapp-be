package com.evandhardspace.chatapp.domain.event

import com.evandhardspace.chatapp.domain.type.ChatId
import com.evandhardspace.chatapp.domain.type.ChatMessageId
import com.evandhardspace.chatapp.domain.type.UserId

sealed interface ModuleChatEvent {

    data class ChatParticipantsJoinedEvent(
        val chatId: ChatId,
        val userIds: Set<UserId>,
    ) : ModuleChatEvent

    data class MessageDeletedEvent(
        val chatId: ChatId,
        val messageId: ChatMessageId,
    ) : ModuleChatEvent

    data class ChatParticipantLeftEvent(
        val chatId: ChatId,
        val userId: UserId,
    ) : ModuleChatEvent
}

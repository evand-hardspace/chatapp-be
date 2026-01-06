package com.evandhardspace.chatapp.domain.event

import com.evandhardspace.chatapp.domain.type.ChatId
import com.evandhardspace.chatapp.domain.type.ChatMessageId
import com.evandhardspace.chatapp.domain.type.UserId

internal sealed interface ModuleChatEvent {

    data class ModuleChatParticipantJoinEvent(
        val chatId: ChatId,
        val userId: Set<UserId>,
    ) : ModuleChatEvent

    data class MessageDeletedEventModule(
        val chatId: ChatId,
        val messageId: ChatMessageId,
    ) : ModuleChatEvent

    data class ModuleChatParticipantLeftEvent(
        val chatId: ChatId,
        val userId: UserId,
    ) : ModuleChatEvent
}
package com.evandhardspace.chatapp.domain.events.chat

import com.evandhardspace.chatapp.domain.events.ChatAppEvent
import com.evandhardspace.chatapp.domain.type.ChatId
import com.evandhardspace.chatapp.domain.type.UserId
import java.time.Instant
import java.util.*

sealed class ChatEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val exchange: String = ChatEventConstants.CHAT_EXCHANGE,
    override val occurredAt: Instant = Instant.now(),
): ChatAppEvent {

    data class NewMessage(
        val senderId: UserId,
        val senderUsername: String,
        val recipientIds: Set<UserId>,
        val chatId: ChatId,
        val messageContent: String,
        override val eventKey: String = ChatEventConstants.CHAT_NEW_MESSAGE,
    ): ChatEvent()
}
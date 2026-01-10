package com.evandhardspace.chatapp.service

import com.evandhardspace.chatapp.domain.event.ModuleChatEvent
import com.evandhardspace.chatapp.domain.events.chat.ChatEvent
import com.evandhardspace.chatapp.domain.exception.ChatMessageNotFoundException
import com.evandhardspace.chatapp.domain.exception.ChatNotFoundException
import com.evandhardspace.chatapp.domain.exception.ChatParticipantNotFoundException
import com.evandhardspace.chatapp.domain.exception.ForbiddenException
import com.evandhardspace.chatapp.domain.model.ChatMessage
import com.evandhardspace.chatapp.domain.type.ChatId
import com.evandhardspace.chatapp.domain.type.ChatMessageId
import com.evandhardspace.chatapp.domain.type.UserId
import com.evandhardspace.chatapp.infra.database.entity.ChatMessageEntity
import com.evandhardspace.chatapp.infra.database.mapper.toChatMessage
import com.evandhardspace.chatapp.infra.database.repository.ChatMessageRepository
import com.evandhardspace.chatapp.infra.database.repository.ChatParticipantRepository
import com.evandhardspace.chatapp.infra.database.repository.ChatRepository
import com.evandhardspace.chatapp.infra.messagequeue.EventPublisher
import com.evandhardspace.chatapp.infra.messagequeue.publishWith
import org.springframework.cache.annotation.CacheEvict
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatMessageService(
    private val chatRepository: ChatRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val eventPublisher: EventPublisher,
) {

    @Transactional
    @CacheEvict(
        value = ["messages"],
        key = "#chatId",
    )
    fun sendMessage(
        chatId: ChatId,
        senderId: UserId,
        content: String,
        messageId: ChatMessageId? = null,
    ): ChatMessage {
        val chat = chatRepository.findChatById(chatId, senderId)
            ?: throw ChatNotFoundException()
        val sender = chatParticipantRepository.findByIdOrNull(senderId)
            ?: throw ChatParticipantNotFoundException(senderId)

        val savedMessage = chatMessageRepository.saveAndFlush(
            ChatMessageEntity(
                id = messageId,
                content = content.trim(),
                chatId = chatId,
                chat = chat,
                sender = sender,
            )
        )

        ChatEvent.NewMessage(
            senderId = sender.userId,
            senderUsername = sender.username,
            recipientIds = chat.participants.map { it.userId }.toSet(),
            chatId = chatId,
            messageContent = savedMessage.content,
        ).publishWith(eventPublisher)

        return savedMessage.toChatMessage()
    }

    @Transactional
    fun deleteMessage(
        messageId: ChatMessageId,
        requestUserId: UserId,
    ) {
        val message = chatMessageRepository.findByIdOrNull(messageId)
            ?: throw ChatMessageNotFoundException(messageId)

        if (message.sender.userId != requestUserId) throw ForbiddenException()

        chatMessageRepository.delete(message)

        applicationEventPublisher.publishEvent(
            ModuleChatEvent.MessageDeletedEvent(
                chatId = message.chatId,
                messageId = messageId,
            ),
        )

        evictMessageCache(message.chatId)
    }

    @CacheEvict(
        value = ["messages"],
        key = "#chatId",
    )
    fun evictMessageCache(chatId: ChatId) { /* NO-OP: Let Spring handle the cache evict */
    }
}

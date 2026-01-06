package com.evandhardspace.chatapp.service

import com.evandhardspace.chatapp.domain.exception.ChatMessageNotFoundException
import com.evandhardspace.chatapp.domain.exception.ChatNotFoundException
import com.evandhardspace.chatapp.domain.exception.ChatParticipantNotFoundException
import com.evandhardspace.chatapp.domain.exception.ForbiddenException
import com.evandhardspace.chatapp.domain.type.ChatId
import com.evandhardspace.chatapp.domain.type.ChatMessageId
import com.evandhardspace.chatapp.domain.type.UserId
import com.evandhardspace.chatapp.infra.database.entity.ChatMessageEntity
import com.evandhardspace.chatapp.infra.database.repository.ChatMessageRepository
import com.evandhardspace.chatapp.infra.database.repository.ChatParticipantRepository
import com.evandhardspace.chatapp.infra.database.repository.ChatRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatMessageService(
    private val chatRepository: ChatRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
) {

    @Transactional
    fun sendMessage(
        chatId: ChatId,
        senderId: UserId,
        content: String,
        messageId: ChatMessageId? = null,
    ): ChatMessageEntity {
        val chat = chatRepository.findChatById(chatId, senderId)
            ?: throw ChatNotFoundException()
        val sender = chatParticipantRepository.findByIdOrNull(senderId)
            ?: throw ChatParticipantNotFoundException(senderId)

        return chatMessageRepository.save(
            ChatMessageEntity(
                id = messageId,
                content = content.trim(),
                chatId = chatId,
                chat = chat,
                sender = sender,
            )
        )
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
    }
}

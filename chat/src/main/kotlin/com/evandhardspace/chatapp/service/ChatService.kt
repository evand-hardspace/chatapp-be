package com.evandhardspace.chatapp.service

import com.evandhardspace.chatapp.domain.exception.ChatNotFoundException
import com.evandhardspace.chatapp.domain.exception.ChatParticipantNotFoundException
import com.evandhardspace.chatapp.domain.exception.ForbiddenException
import com.evandhardspace.chatapp.domain.exception.InvalidChatSizeException
import com.evandhardspace.chatapp.domain.model.Chat
import com.evandhardspace.chatapp.domain.model.ChatMessage
import com.evandhardspace.chatapp.domain.type.ChatId
import com.evandhardspace.chatapp.domain.type.UserId
import com.evandhardspace.chatapp.infra.database.entity.ChatEntity
import com.evandhardspace.chatapp.infra.database.mapper.toChat
import com.evandhardspace.chatapp.infra.database.mapper.toChatMessage
import com.evandhardspace.chatapp.infra.database.repository.ChatMessageRepository
import com.evandhardspace.chatapp.infra.database.repository.ChatParticipantRepository
import com.evandhardspace.chatapp.infra.database.repository.ChatRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository,
) {

    @Transactional
    fun createChat(
        creatorId: UserId,
        otherUserIds: Set<UserId>,
    ): Chat {
        val otherParticipants = chatParticipantRepository.findByUserIdIn(
            userIds = otherUserIds,
        )

        val allParticipants = (otherParticipants + creatorId)
        if (allParticipants.size < 2) throw InvalidChatSizeException()

        val creator = chatParticipantRepository.findByIdOrNull(creatorId)
            ?: throw ChatParticipantNotFoundException(creatorId)

        return chatRepository.save(
            ChatEntity(
                creator = creator,
                participants = (otherParticipants + creator).toMutableSet(),
            ),
        ).toChat(lastMessage = null)
    }

    @Transactional
    fun addParticipantsToChat(
        requestUserId: UserId,
        chatId: ChatId,
        userIds: Set<UserId>,
    ): Chat {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()
        val isRequestingUserInChat = chat.participants.any { it.userId == requestUserId }

        if (isRequestingUserInChat.not()) throw ForbiddenException()

        val users = chatParticipantRepository.findByUserIdIn(userIds)

        val missingIds = users.map { it.userId } - userIds
        if (missingIds.isNotEmpty()) throw ChatParticipantNotFoundException(missingIds.first())

        val lastMessage = lastMessageForChat(chatId)
        return chatRepository.save(
            chat.apply {
                this.participants = (chat.participants + users).toMutableSet()
            }
        ).toChat(lastMessage)
    }

    @Transactional
    fun removeParticipantFromChat(
        chatId: ChatId,
        userId: UserId,
    ) {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()
        val participant = chat.participants.find { it.userId == userId }
            ?: throw ChatParticipantNotFoundException(userId)

        val newParticipantsSize = chat.participants.size - 1
        if (newParticipantsSize == 0) {
            chatRepository.deleteById(chatId)
            return
        }

        chatRepository.save(
            chat.apply {
                this.participants = (chat.participants - participant).toMutableSet()
            }
        )
    }

    private fun lastMessageForChat(chatId: ChatId): ChatMessage? =
        chatMessageRepository
            .findLatestMessagesByChatIds(setOf(chatId))
            .firstOrNull()
            ?.toChatMessage()
}

package com.evandhardspace.chatapp.service

import com.evandhardspace.chatapp.domain.model.ChatParticipant
import com.evandhardspace.chatapp.domain.type.UserId
import com.evandhardspace.chatapp.infra.database.mapper.toChatParticipant
import com.evandhardspace.chatapp.infra.database.mapper.toChatParticipantEntity
import com.evandhardspace.chatapp.infra.database.repository.ChatParticipantRepository
import com.evandhardspace.chatapp.util.NormalizedString
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatParticipantService(
    private val chatParticipantRepository: ChatParticipantRepository,
) {

    fun createChatParticipant(
        chatParticipant: ChatParticipant,
    ) {
        chatParticipantRepository.save(
            chatParticipant.toChatParticipantEntity(),
        )
    }

    fun findChatParticipantById(
        userId: UserId,
    ): ChatParticipant? = chatParticipantRepository.findByIdOrNull(userId)?.toChatParticipant()

    fun findChatParticipantByEmailOrUsername(
        query: NormalizedString,
    ): ChatParticipant? = chatParticipantRepository.findByEmailOrUsername(
        query = query.value,
    )?.toChatParticipant()
}

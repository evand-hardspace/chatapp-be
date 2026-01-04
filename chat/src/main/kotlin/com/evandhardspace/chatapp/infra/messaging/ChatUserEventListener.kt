package com.evandhardspace.chatapp.infra.messaging

import com.evandhardspace.chatapp.domain.events.user.UserEvent
import com.evandhardspace.chatapp.domain.model.ChatParticipant
import com.evandhardspace.chatapp.infra.messagequeue.MessageQueueConstants
import com.evandhardspace.chatapp.service.ChatParticipantService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ChatUserEventListener(
    private val chatParticipantService: ChatParticipantService,
) {

    @RabbitListener(queues = [MessageQueueConstants.CHAT_USER_EVENTS])
    @Transactional
    fun handleUserEvent(event: UserEvent): Unit = when (event) {
        is UserEvent.Verified -> {

            chatParticipantService.createChatParticipant(
                chatParticipant = ChatParticipant(
                    userId = event.userId,
                    username = event.username,
                    email = event.email,
                    profilePictureUrl = null,
                )
            )
        }

        is UserEvent.Created,
        is UserEvent.RequestResendVerification,
        is UserEvent.RequestResetPassword -> Unit
    }
}

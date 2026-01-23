package com.evandhardspace.chatapp.infra.messagequeue

import com.evandhardspace.chatapp.domain.events.chat.ChatEvent
import com.evandhardspace.chatapp.domain.events.user.UserEvent
import com.evandhardspace.chatapp.service.EmailService
import com.evandhardspace.chatapp.service.PushNotificationService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Component
class NotificationChatEventListener(
    private val pushNotificationService: PushNotificationService,
) {

    @RabbitListener(queues = [MessageQueueConstants.NOTIFICATION_CHAT_EVENTS])
    @Transactional
    fun handleUserEvent(event: ChatEvent): Unit = when (event) {
        is ChatEvent.NewMessage -> {
            pushNotificationService.sendNewMessageNotifications(
                recipientUserIds = event.recipientIds.toList(),
                senderUserId = event.senderId,
                senderUsername = event.senderUsername,
                message = event.messageContent,
                chatId = event.chatId,
            )
        }
    }
}

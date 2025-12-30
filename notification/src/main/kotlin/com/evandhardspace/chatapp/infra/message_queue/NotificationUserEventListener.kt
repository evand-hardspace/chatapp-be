package com.evandhardspace.chatapp.infra.message_queue

import com.evandhardspace.chatapp.domain.events.user.UserEvent
import com.evandhardspace.chatapp.infra.messagequeue.MessageQueueConstants
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationUserEventListener {

    @RabbitListener(queues = [MessageQueueConstants.NOTIFICATION_USER_EVENTS])
    @Transactional
    fun handleUserEvent(event: UserEvent): Unit = when (event) {
        is UserEvent.Created -> {
            println("User created!")
        }

        is UserEvent.RequestResendVerification -> {
            println("Request resend verification!")
        }

        is UserEvent.RequestResetPassword -> {
            println("Request resend password!")
        }

        is UserEvent.Verified -> Unit
    }
}

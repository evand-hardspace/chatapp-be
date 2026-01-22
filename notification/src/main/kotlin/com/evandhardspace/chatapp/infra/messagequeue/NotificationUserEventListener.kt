package com.evandhardspace.chatapp.infra.messagequeue

import com.evandhardspace.chatapp.domain.events.user.UserEvent
import com.evandhardspace.chatapp.service.EmailService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Component
class NotificationUserEventListener(private val emailService: EmailService) {

    @RabbitListener(queues = [MessageQueueConstants.NOTIFICATION_USER_EVENTS])
    @Transactional
    fun handleUserEvent(event: UserEvent): Unit = when (event) {
        is UserEvent.Created -> {
            emailService.sendVerificationEmail(
                email = event.email,
                username = event.username,
                userId = event.userId,
                token = event.verificationToken,
            )
        }

        is UserEvent.RequestResendVerification -> {
            emailService.sendVerificationEmail(
                email = event.email,
                username = event.username,
                userId = event.userId,
                token = event.verificationToken,
            )
        }

        is UserEvent.RequestResetPassword -> {
            emailService.sendPasswordResetEmail(
                email = event.email,
                username = event.username,
                userId = event.userId,
                token = event.passwordResetToken,
                expiresIn = event.expiresInMinutes.run(Duration::ofMinutes),
            )
        }

        is UserEvent.Verified -> Unit
    }
}

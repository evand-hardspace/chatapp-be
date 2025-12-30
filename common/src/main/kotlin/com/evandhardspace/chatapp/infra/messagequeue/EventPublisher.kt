package com.evandhardspace.chatapp.infra.messagequeue

import com.evandhardspace.chatapp.domain.events.ChatAppEvent
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class EventPublisher(
    private val rabbitTemplate: RabbitTemplate
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun <T : ChatAppEvent> publish(event: T) {
        try {
            rabbitTemplate.convertAndSend(
                /* exchange = */ event.exchange,
                /* routingKey = */ event.eventKey,
                /* object = */ event,
            )
            logger.info("Successfully published event: ${event.eventKey}")
        } catch (e: Exception) {
            logger.error("Failed to publish ${event.eventKey} event", e)
        }
    }
}

fun <T: ChatAppEvent> T.publishWith(publisher: EventPublisher) = publisher.publish(this)

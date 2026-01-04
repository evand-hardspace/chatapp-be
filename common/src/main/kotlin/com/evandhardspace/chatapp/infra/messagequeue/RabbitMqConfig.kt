package com.evandhardspace.chatapp.infra.messagequeue

import com.evandhardspace.chatapp.domain.events.ChatAppEvent
import com.evandhardspace.chatapp.domain.events.chat.ChatEventConstants
import com.evandhardspace.chatapp.domain.events.user.UserEventConstants
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement
import tools.jackson.databind.DefaultTyping
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import tools.jackson.module.kotlin.kotlinModule

@Configuration
@EnableTransactionManagement
class RabbitMqConfig {

    @Bean
    fun messageConverter(): JacksonJsonMessageConverter {
        val polymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType(ChatAppEvent::class.java)
            .allowIfSubType("java.util.") // Allow Java lists
            .allowIfSubType("kotlin.collections.") // Kotlin collections
            .build()

        val objectMapper = JsonMapper.builder()
            .addModule(kotlinModule())
            .polymorphicTypeValidator(polymorphicTypeValidator)
            .activateDefaultTyping(polymorphicTypeValidator, DefaultTyping.NON_FINAL)
            .build()

        return JacksonJsonMessageConverter(objectMapper).apply {
            typePrecedence = JacksonJavaTypeMapper.TypePrecedence.TYPE_ID
        }
    }

    @Bean
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        messageConverter: JacksonJsonMessageConverter,
    ): RabbitTemplate = RabbitTemplate(connectionFactory).apply {
        this.messageConverter = messageConverter
    }

    @Bean
    fun userExchange(): TopicExchange = TopicExchange(
        UserEventConstants.USER_EXCHANGE,
        true,
        false,
    )

    @Bean
    fun chatExchange(): TopicExchange = TopicExchange(
        ChatEventConstants.CHAT_EXCHANGE,
        true,
        false,
    )

    @Bean
    fun notificationUserEventsQueue(): Queue = Queue(
        MessageQueueConstants.NOTIFICATION_USER_EVENTS,
        true,
    )

    @Bean
    fun chatUserEventsQueue(): Queue = Queue(
        MessageQueueConstants.CHAT_USER_EVENTS,
        true,
    )

    @Bean
    fun notificationUserEventsBinding(
        notificationUserEventsQueue: Queue,
        userExchange: TopicExchange,
    ): Binding = BindingBuilder
        .bind(notificationUserEventsQueue)
        .to(userExchange)
        .with("user.*")


    @Bean
    fun chatUserEventsBinding(
        chatUserEventsQueue: Queue,
        userExchange: TopicExchange,
    ): Binding = BindingBuilder
        .bind(chatUserEventsQueue)
        .to(userExchange)
        .with("user.*")
}

package com.evandhardspace.chatapp.api.util

import com.evandhardspace.chatapp.domain.event.ModuleChatEvent
import org.springframework.context.ApplicationEventPublisher

internal fun ModuleChatEvent.publishWith(publisher: ApplicationEventPublisher): Unit =
    publisher.publishEvent(this)

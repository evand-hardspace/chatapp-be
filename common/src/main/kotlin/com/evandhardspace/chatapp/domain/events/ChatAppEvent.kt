package com.evandhardspace.chatapp.domain.events

import java.time.Instant

interface ChatAppEvent {
    val eventId: String
    val eventKey: String
    val occurredAt: Instant
    val exchange: String
}

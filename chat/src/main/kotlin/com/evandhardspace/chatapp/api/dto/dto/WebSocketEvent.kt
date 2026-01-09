package com.evandhardspace.chatapp.api.dto.dto

enum class IncomingWebSocketEventType {
    NewMessage,
}

enum class OutgoingWebSocketEventType {
    NewMessage,
    MessageDeleted,
    ProfilePictureUpdated,
    ChatParticipantsChanged,
    Error,
}

// TODO (6)
data class IncomingWebSocketMessage(
    val type: IncomingWebSocketEventType,
    val payload: String,
)

// TODO (6)
data class OutgoingWebSocketMessage(
    val type: OutgoingWebSocketEventType,
    val payload: String,
)
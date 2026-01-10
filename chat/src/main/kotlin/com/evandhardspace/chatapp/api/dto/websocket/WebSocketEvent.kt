package com.evandhardspace.chatapp.api.dto.websocket

enum class IncomingWebSocketMessageType {
    NewMessage,
}

enum class OutgoingWebSocketMessageType {
    NewMessage,
    MessageDeleted,
    ProfilePictureUpdated,
    ChatParticipantsChanged,
    Error,
}

// TODO (6)
data class IncomingWebSocketMessage(
    val type: IncomingWebSocketMessageType,
    val payload: String,
)

// TODO (6)
data class OutgoingWebSocketMessage(
    val type: OutgoingWebSocketMessageType,
    val payload: String,
)
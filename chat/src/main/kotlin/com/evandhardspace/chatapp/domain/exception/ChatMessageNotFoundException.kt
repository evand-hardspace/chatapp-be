package com.evandhardspace.chatapp.domain.exception

import com.evandhardspace.chatapp.domain.type.ChatMessageId

class ChatMessageNotFoundException(id: ChatMessageId) : RuntimeException(
    "Chat message with ID: $id not found.",
)

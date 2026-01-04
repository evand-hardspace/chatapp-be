package com.evandhardspace.chatapp.domain.exception

class InvalidChatSizeException: RuntimeException(
    "There must me at least 2 unique participants to create a chat.",
)
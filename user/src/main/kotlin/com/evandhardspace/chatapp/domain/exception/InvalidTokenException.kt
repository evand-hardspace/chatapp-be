package com.evandhardspace.chatapp.domain.exception

class InvalidTokenException(
    override val message: String?
) : RuntimeException(message ?: "Invalid token")

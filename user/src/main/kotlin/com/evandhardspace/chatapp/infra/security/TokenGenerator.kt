package com.evandhardspace.chatapp.infra.security

import java.security.SecureRandom
import java.util.Base64

object TokenGenerator {
    fun generateSecureToken(): String = ByteArray(32) { 0 }.let { bytes ->
        SecureRandom().nextBytes(bytes)
        Base64.getEncoder()
            .withoutPadding()
            .encodeToString(bytes)
    }
}
package com.evandhardspace.chatapp.infra.security

import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64

@Component
object TokenGenerator {
    fun generateSecureToken(): String = ByteArray(32) { 0 }.let { bytes ->
        SecureRandom().nextBytes(bytes)
        Base64.getEncoder()
            .withoutPadding()
            .encodeToString(bytes)
    }
}
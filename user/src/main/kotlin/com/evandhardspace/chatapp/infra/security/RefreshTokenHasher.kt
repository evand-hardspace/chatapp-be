package com.evandhardspace.chatapp.infra.security

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.Base64

@Component
class RefreshTokenHasher {

    private val digest = MessageDigest.getInstance("SHA-256")

    fun hash(token: String): String =
        Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(digest.digest(token.toByteArray()))
}

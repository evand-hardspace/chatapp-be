package com.evandhardspace.chatapp.infra.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import com.evandhardspace.chatapp.util.requireNotNull

@Component
class PasswordEncoder {

    private val bcrypt = BCryptPasswordEncoder()

    fun encode(rawPassword: String): String =
        bcrypt.encode(rawPassword).requireNotNull { "Password cannot be null" }

    fun matches(rawPassword: String, hashedPassword: String): Boolean =
        bcrypt.matches(rawPassword, hashedPassword)


}
package com.evandhardspace.chatapp.service.auth

import com.evandhardspace.chatapp.domain.exception.UserAlreadyExistsException
import com.evandhardspace.chatapp.domain.model.User
import com.evandhardspace.chatapp.infra.database.entity.UserEntity
import com.evandhardspace.chatapp.infra.database.mapper.toUser
import com.evandhardspace.chatapp.infra.repository.UserRepository
import com.evandhardspace.chatapp.infra.security.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun register(email: String, username: String, password: String): User {
        userRepository.findByEmailOrUsername(
            email = email.trim(),
            username = username.trim(),
        )?.run { throw UserAlreadyExistsException() }

        return userRepository.save(
            UserEntity(
                email = email.trim(),
                username = username.trim(),
                hasVerifiedEmail = false,
                hashedPassword = passwordEncoder.encode(password),
            )
        ).toUser()
    }
}

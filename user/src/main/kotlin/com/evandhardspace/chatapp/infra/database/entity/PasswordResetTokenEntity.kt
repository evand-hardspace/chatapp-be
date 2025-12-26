package com.evandhardspace.chatapp.infra.database.entity

import com.evandhardspace.chatapp.infra.security.TokenGenerator
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(
    name = "password_reset_tokens",
    schema = "user_service",
    indexes = [
        Index(name = "idx_password_reset_token_token", columnList = "token"),
    ]
)
class PasswordResetTokenEntity(
    @[Id GeneratedValue(strategy = GenerationType.IDENTITY)]
    var id: Long = 0,
    @Column(nullable = false)
    var token: String = TokenGenerator.generateSecureToken(),
    @[ManyToOne(fetch = FetchType.LAZY) JoinColumn(name = "user_id", nullable = false)]
    var user: UserEntity,
    @Column(nullable = false)
    var expiresAt: Instant,
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),
    @Column(nullable = true)
    var usedAt: Instant? = null,
)

val PasswordResetTokenEntity.isUsed: Boolean
    get() = usedAt != null

fun PasswordResetTokenEntity.isExpired(instant: Instant): Boolean = instant > expiresAt

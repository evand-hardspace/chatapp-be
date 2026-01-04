package com.evandhardspace.chatapp.api.util

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@Size(
    min = 1,
    max = 50,
    message = "Chat must have at least 2 unique participants, but not more than 50.",
)
annotation class ChatParticipantSize(
    val message: String = "Chat must have at least 2 unique participants, but not more than 50.",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

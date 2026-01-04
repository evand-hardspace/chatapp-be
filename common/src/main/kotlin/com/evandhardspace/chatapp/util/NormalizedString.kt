package com.evandhardspace.chatapp.util


/**
 * Trimmed and lowercased string.
 */
@JvmInline
value class NormalizedString private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String) = NormalizedString(value.lowercase().trim())
    }
}

fun String.normalized() = NormalizedString(this)
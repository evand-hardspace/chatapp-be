package com.evandhardspace.chatapp.domain.events.user

object UserEventsConstants {
    const val USER_EXCHANGE = "user.events"

    const val USER_CREATED_KEY = "user.created"
    const val USER_VERIFIED = "user.verified"
    const val USER_REQUEST_RESEND_VERIFICATION_KEY = "user.request_resend_verification"
    const val USER_REQUEST_RESET_PASSWORD_KEY = "user.request_reset_password"
}

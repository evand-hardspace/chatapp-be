package com.evandhardspace.chatapp.domain.exception

class RateLimitException(resetsInSeconds: Long) : RuntimeException(
    "Rate limit exceeded. Please try again in $resetsInSeconds seconds.",
)
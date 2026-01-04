package com.evandhardspace.chatapp.domain.exception

class UnauthorizedException : RuntimeException(
    "Missing authentication details.",
)
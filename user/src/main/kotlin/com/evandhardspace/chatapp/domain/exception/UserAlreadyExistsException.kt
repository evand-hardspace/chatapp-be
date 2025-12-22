package com.evandhardspace.chatapp.domain.exception

class UserAlreadyExistsException : RuntimeException(
    "A user with this username or email already exists.",
)

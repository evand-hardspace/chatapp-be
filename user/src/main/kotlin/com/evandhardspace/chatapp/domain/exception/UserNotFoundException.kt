package com.evandhardspace.chatapp.domain.exception

class UserNotFoundException : RuntimeException(
    "User not found."
)
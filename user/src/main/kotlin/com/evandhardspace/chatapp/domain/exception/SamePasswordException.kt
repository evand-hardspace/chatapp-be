package com.evandhardspace.chatapp.domain.exception

class SamePasswordException : RuntimeException(
     "The new password cannot be equal to the old password.",
)

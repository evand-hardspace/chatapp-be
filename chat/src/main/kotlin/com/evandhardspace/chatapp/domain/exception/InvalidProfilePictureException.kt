package com.evandhardspace.chatapp.domain.exception

class InvalidProfilePictureException(message: String? = null) :
    RuntimeException(message ?: "Invalid profile picture data.")

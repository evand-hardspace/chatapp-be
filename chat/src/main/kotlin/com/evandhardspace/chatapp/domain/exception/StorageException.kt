package com.evandhardspace.chatapp.domain.exception

class StorageException(message: String? = null) :
    RuntimeException(message ?: "Unable to store file.")

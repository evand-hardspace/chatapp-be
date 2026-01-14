package com.evandhardspace.chatapp.api.exceptionhandling

import com.evandhardspace.chatapp.api.exceptionhanding.toHandlerResponse
import com.evandhardspace.chatapp.domain.exception.ChatMessageNotFoundException
import com.evandhardspace.chatapp.domain.exception.ChatNotFoundException
import com.evandhardspace.chatapp.domain.exception.ChatParticipantNotFoundException
import com.evandhardspace.chatapp.domain.exception.InvalidChatSizeException
import com.evandhardspace.chatapp.domain.exception.InvalidProfilePictureException
import com.evandhardspace.chatapp.domain.exception.StorageException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ChatExceptionHandler {

    @ExceptionHandler(
        ChatMessageNotFoundException::class,
        ChatNotFoundException::class,
        ChatParticipantNotFoundException::class,
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onChatMessageNotFound(e: RuntimeException) =
        e.toHandlerResponse(code = "NOT_FOUND")

    @ExceptionHandler(InvalidChatSizeException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onInvalidChatSize(e: InvalidChatSizeException) =
        e.toHandlerResponse(code = "INVALID_CHAT_SIZE")

    @ExceptionHandler(InvalidProfilePictureException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onInvalidProfilePicture(e: InvalidProfilePictureException) =
        e.toHandlerResponse(code = "INVALID_PROFILE_PICTURE")

    @ExceptionHandler(StorageException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun onStorageException(e: StorageException) =
        e.toHandlerResponse(code = "STORAGE_ERROR")
}

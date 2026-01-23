package com.evandhardspace.chatapp.api.exceptionhandling

import com.evandhardspace.chatapp.api.exceptionhanding.toHandlerResponse
import com.evandhardspace.chatapp.domain.model.InvalidDeviceTokenException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class NotificationExceptionHandler {

    @ExceptionHandler(InvalidDeviceTokenException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onInvalidDeviceToken(e: InvalidDeviceTokenException) =
        e.toHandlerResponse("INVALID_DEVICE_TOKEN")
}

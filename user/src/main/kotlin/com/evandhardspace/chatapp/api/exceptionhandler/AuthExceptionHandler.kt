package com.evandhardspace.chatapp.api.exceptionhandler

import com.evandhardspace.chatapp.domain.exception.UserAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun onUserAlreadyExists(e: UserAlreadyExistsException): Map<String, Any> = mapOf(
        "code" to "User exists",
        "message" to e.message.orEmpty(),
    )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onValidationException(e: MethodArgumentNotValidException): Map<String, Any> = mapOf(
        "code" to "Validation error",
        "errors" to e.bindingResult.allErrors.map {
            it.defaultMessage ?: "Invalid value"
        },
    )
}

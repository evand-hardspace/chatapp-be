package com.evandhardspace.chatapp.api.exceptionhandler

import com.evandhardspace.chatapp.domain.exception.InvalidCredentialsException
import com.evandhardspace.chatapp.domain.exception.InvalidTokenException
import com.evandhardspace.chatapp.domain.exception.UserAlreadyExistsException
import com.evandhardspace.chatapp.domain.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun onUserAlreadyExists(e: UserAlreadyExistsException): Map<String, Any?> = mapOf(
        "code" to "USER_EXISTS",
        "message" to e.message,
    )

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onUserNotFound(e: UserNotFoundException): Map<String, Any?> = mapOf(
        "code" to "USER_NOT_FOUND",
        "message" to e.message,
    )

    @ExceptionHandler(InvalidCredentialsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun onInvalidCredentials(e: InvalidCredentialsException): Map<String, Any?> = mapOf(
        "code" to "INVALID_CREDENTIALS ",
        "message" to e.message,
    )

    @ExceptionHandler(InvalidTokenException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onInvalidToken(e: InvalidTokenException): Map<String, Any?> = mapOf(
        "code" to "INVALID_TOKEN",
        "message" to e.message,
    )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onValidationException(e: MethodArgumentNotValidException): Map<String, Any> = mapOf(
        "code" to "VALIDATION_ERROR",
        "errors" to e.bindingResult.allErrors.map {
            it.defaultMessage ?: "Invalid value"
        },
    )
}

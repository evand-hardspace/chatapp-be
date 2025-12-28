package com.evandhardspace.chatapp.api.exceptionhandler

import com.evandhardspace.chatapp.domain.exception.EmailNotVerifiedException
import com.evandhardspace.chatapp.domain.exception.InvalidCredentialsException
import com.evandhardspace.chatapp.domain.exception.InvalidTokenException
import com.evandhardspace.chatapp.domain.exception.RateLimitException
import com.evandhardspace.chatapp.domain.exception.SamePasswordException
import com.evandhardspace.chatapp.domain.exception.UnauthorizedException
import com.evandhardspace.chatapp.domain.exception.UserAlreadyExistsException
import com.evandhardspace.chatapp.domain.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onValidationException(e: MethodArgumentNotValidException): Map<String, Any> = mapOf(
        "code" to "VALIDATION_ERROR",
        "errors" to e.bindingResult.allErrors.map {
            it.defaultMessage ?: "Invalid value"
        },
    )

    @ExceptionHandler(UserAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun onUserAlreadyExists(e: UserAlreadyExistsException): Map<String, Any> =
        e.asResponse(code = "USER_EXISTS")

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onUserNotFound(e: UserNotFoundException): Map<String, Any> =
        e.asResponse(code = "USER_NOT_FOUND")

    @ExceptionHandler(InvalidCredentialsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun onInvalidCredentials(e: InvalidCredentialsException): Map<String, Any> =
        e.asResponse(code = "INVALID_CREDENTIALS")

    @ExceptionHandler(InvalidTokenException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onInvalidToken(e: InvalidTokenException): Map<String, Any> =
        e.asResponse(code = "INVALID_TOKEN")

    @ExceptionHandler(EmailNotVerifiedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onEmailNotVerified(e: EmailNotVerifiedException): Map<String, Any> =
        e.asResponse(code = "EMAIL_NOT_VERIFIED")

    @ExceptionHandler(SamePasswordException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun onSamePassword(e: SamePasswordException): Map<String, Any> =
        e.asResponse(code = "SAME_PASSWORD")

    @ExceptionHandler(RateLimitException::class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    fun onRateLimitExceeded(e: RateLimitException): Map<String, Any> =
        e.asResponse(code = "RATE_LIMIT_EXCEEDED")

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onUnauthorized(e: UnauthorizedException): Map<String, Any> =
        e.asResponse(code = "UNAUTHORIZED")
}

private fun RuntimeException.asResponse(code: String): Map<String, Any> = buildMap {
    put("code", code)
    message?.let { put("message", it) }
}

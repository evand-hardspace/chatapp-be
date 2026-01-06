package com.evandhardspace.chatapp.api.exceptionhanding

import com.evandhardspace.chatapp.domain.exception.ForbiddenException
import com.evandhardspace.chatapp.domain.exception.UnauthorizedException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CommonExceptionHandler {

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun onForbidden(e: ForbiddenException) =
        e.toHandlerResponse(code = "FORBIDDEN")

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onUnauthorized(e: UnauthorizedException) =
        e.toHandlerResponse(code = "UNAUTHORIZED")
}

package com.adventistportal.core.api.exception_handling

import com.adventistportal.core.domain.exceptions.ForbiddenEx
import com.adventistportal.core.domain.exceptions.UnauthorizedEx
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CommonExceptionHandling(

) {
    @ExceptionHandler(ForbiddenEx::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun onForbidden(
        e: ForbiddenEx
    ) = mapOf(
        "code" to "FORBIDDEN",
        "message" to e.message
    )

    @ExceptionHandler(UnauthorizedEx::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun onUnauthorized(
        e: UnauthorizedEx
    ) = mapOf(
        "code" to "UNAUTHORIZED",
        "message" to e.message
    )

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onIllegalArgument(
        e: IllegalArgumentException
    ) = mapOf(
        "code" to "BAD_REQUEST",
        "message" to e.message
    )
}
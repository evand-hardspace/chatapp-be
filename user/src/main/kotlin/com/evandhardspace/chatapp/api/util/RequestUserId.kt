package com.evandhardspace.chatapp.api.util

import com.evandhardspace.chatapp.domain.exception.UnauthorizedException
import com.evandhardspace.chatapp.domain.model.UserId
import org.springframework.security.core.context.SecurityContextHolder

val requestUserId: UserId
    get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
        ?: throw UnauthorizedException()
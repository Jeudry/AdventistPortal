package com.adventistportal.core.api.utils

import com.adventistportal.core.domain.exceptions.UnauthorizedEx
import com.adventistportal.core.domain.types.UserId
import org.springframework.security.core.context.SecurityContextHolder

val requestUserId: UserId
    get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
        ?: throw UnauthorizedEx()
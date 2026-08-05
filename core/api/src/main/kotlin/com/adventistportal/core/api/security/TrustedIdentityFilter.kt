package com.adventistportal.core.api.security

import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

/**
 * The service-side half of gateway authentication: the token was verified at the edge, so
 * all that arrives here is who the caller is.
 *
 * This trusts its input completely. It is only sound while the service port is reachable
 * from the gateway alone — expose it and anyone can send the header.
 */
@Component
class TrustedIdentityFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        request.getHeader(USER_ID_HEADER)
            ?.let(::asUserId)
            ?.let { SecurityContextHolder.getContext().authentication = it.asAuthentication() }

        filterChain.doFilter(request, response)
    }

    private fun asUserId(header: String): UUID? = runCatching { UUID.fromString(header) }.getOrNull()

    private fun UUID.asAuthentication() = UsernamePasswordAuthenticationToken(this, null, emptyList())
}

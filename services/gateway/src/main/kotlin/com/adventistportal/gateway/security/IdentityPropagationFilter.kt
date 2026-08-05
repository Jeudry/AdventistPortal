package com.adventistportal.gateway.security

import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import com.adventistportal.core.services.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Collections
import java.util.Enumeration

/**
 * The one place a token is read. Services behind the gateway never see it: they receive
 * [USER_ID_HEADER] and trust it.
 *
 * This answers *who is calling*, not *may they call*. Which endpoints are reachable
 * without a caller stays with the service that owns them — kept here too, it would be a
 * second list of paths that drifts from the first. So a request with no token is
 * forwarded as anonymous and refused downstream. A request with a *broken* token is
 * refused here, because that is an error under any policy.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class IdentityPropagationFilter(private val jwtService: JwtService) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = request.bearerToken()

        if (token != null && !jwtService.validateAccessToken(token)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value())
            return
        }

        val userId = token?.let(jwtService::getUserIdFromToken)?.toString()
        filterChain.doFilter(WithAssertedIdentity(request, userId), response)
    }

    private fun HttpServletRequest.bearerToken(): String? =
        getHeader(HttpHeaders.AUTHORIZATION)
            ?.takeIf { it.startsWith(BEARER_PREFIX) }
            ?.removePrefix(BEARER_PREFIX)

    private companion object {
        const val BEARER_PREFIX = "Bearer "
    }
}

/**
 * Replaces [USER_ID_HEADER] rather than adding to it. A client that sends its own copy
 * has it dropped here, which is the only thing making the header trustworthy downstream.
 */
private class WithAssertedIdentity(
    request: HttpServletRequest,
    private val userId: String?,
) : HttpServletRequestWrapper(request) {

    override fun getHeader(name: String): String? =
        if (name.isIdentity()) userId else super.getHeader(name)

    override fun getHeaders(name: String): Enumeration<String> =
        if (name.isIdentity()) {
            Collections.enumeration(listOfNotNull(userId))
        } else {
            super.getHeaders(name)
        }

    override fun getHeaderNames(): Enumeration<String> {
        val passedThrough = super.getHeaderNames().toList().filterNot { it.isIdentity() }
        return Collections.enumeration(passedThrough + listOfNotNull(userId?.let { USER_ID_HEADER }))
    }

    private fun String.isIdentity() = equals(USER_ID_HEADER, ignoreCase = true)
}

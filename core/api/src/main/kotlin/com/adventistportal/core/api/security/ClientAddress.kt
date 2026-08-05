package com.adventistportal.core.api.security

import com.adventistportal.core.domain.security.TrustedIdentity.CLIENT_ADDRESS_HEADER
import jakarta.servlet.http.HttpServletRequest

/**
 * Who the request actually came from, as opposed to who handed it over.
 *
 * Behind the gateway `remoteAddr` is the gateway, so anything keyed on it — a rate limit,
 * most obviously — puts every user in the world in the same bucket, and one of them
 * hitting the limit locks out all of them. The gateway asserts the real one.
 *
 * That header is trusted for the same reason [TrustedIdentity][com.adventistportal.core.domain.security.TrustedIdentity]
 * is: nothing reaches this service except through the gateway, which replaces it. Expose a
 * service port directly and both become a client's opinion of itself.
 */
object ClientAddress {

    fun of(request: HttpServletRequest): String = request
        .getHeader(CLIENT_ADDRESS_HEADER)
        ?.takeIf(String::isNotBlank)
        ?: request.remoteAddr
}

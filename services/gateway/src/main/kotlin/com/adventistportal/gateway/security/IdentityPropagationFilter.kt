package com.adventistportal.gateway.security

import com.adventistportal.core.domain.security.TrustedIdentity.USER_ID_HEADER
import com.adventistportal.core.services.JwtService
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 * The one place a token is read. Services behind the gateway never see it: they receive
 * [USER_ID_HEADER] and trust it.
 *
 * This answers *who is calling*, not *may they call*. Which endpoints are reachable
 * without a caller stays with the service that owns them — kept here too, it would be a
 * second list of paths that drifts from the first. So a request with no token is
 * forwarded as anonymous and refused downstream. A request with a *broken* token is
 * refused here, because that is an error under any policy.
 *
 * WebSocket handshakes come through here as well, which is the point of the gateway
 * running on the reactive stack: chat used to have to verify that one itself.
 */
@Component
class IdentityPropagationFilter(private val jwtService: JwtService) : WebFilter, Ordered {

    // After the API key: a request with no business being here should not have its
    // token parsed.
    override fun getOrder() = Ordered.HIGHEST_PRECEDENCE + 1

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = exchange.request.bearerToken()

        if (token != null && !jwtService.validateAccessToken(token)) {
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            return exchange.response.setComplete()
        }

        val userId = token?.let(jwtService::getUserIdFromToken)?.toString()
        return chain.filter(exchange.withAssertedIdentity(userId))
    }

    /**
     * Replaces [USER_ID_HEADER] rather than adding to it. A client that sends its own copy
     * has it dropped here, which is the only thing making the header trustworthy
     * downstream.
     */
    private fun ServerWebExchange.withAssertedIdentity(userId: String?): ServerWebExchange {
        val request = request.mutate()
            .headers { it.remove(USER_ID_HEADER) }
            .apply { userId?.let { header(USER_ID_HEADER, it) } }
            .build()

        return mutate().request(request).build()
    }

    private fun ServerHttpRequest.bearerToken(): String? = headers
        .getFirst(HttpHeaders.AUTHORIZATION)
        ?.takeIf { it.startsWith(BEARER_PREFIX) }
        ?.removePrefix(BEARER_PREFIX)
        // A browser cannot set headers on a WebSocket handshake, so the token arrives as a
        // query parameter there. It is still read in this one place.
        ?: queryParams.getFirst(TOKEN_PARAMETER)

    private companion object {
        const val BEARER_PREFIX = "Bearer "
        const val TOKEN_PARAMETER = "access_token"
    }
}

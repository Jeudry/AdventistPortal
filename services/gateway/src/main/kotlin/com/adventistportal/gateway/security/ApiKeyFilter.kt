package com.adventistportal.gateway.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.security.MessageDigest

@ConfigurationProperties(prefix = "gateway.api-key")
data class ApiKeyProperties(
    /** No default. A gateway that would accept anything should not start. */
    val value: String,
    /** Paths answered without one, for the things that ask whether the gateway is alive. */
    val exemptPaths: List<String> = listOf("/actuator/health"),
)

/**
 * Refuses anything that does not come from a build of our own client.
 *
 * Worth being clear about what this buys: the key ships inside the app, so anyone willing
 * to unpack it has it. It is not a security boundary and nothing behind it may treat it as
 * one — the token is what says who you are. What it does is keep the surface off the open
 * internet: scanners, scrapers and traffic aimed at somebody else stop here rather than
 * at a handler.
 *
 * It runs before identity for that reason: a request with no business being here should
 * not have its token parsed.
 */
@Component
class ApiKeyFilter(private val properties: ApiKeyProperties) : WebFilter, Ordered {

    private val expected = properties.value.toByteArray()

    override fun getOrder() = Ordered.HIGHEST_PRECEDENCE

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.uri.path
        if (properties.exemptPaths.any(path::startsWith) || exchange.request.presentsAValidKey()) {
            return chain.filter(exchange)
        }

        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        return exchange.response.setComplete()
    }

    /**
     * Compared in constant time. A comparison that returns early tells whoever is guessing
     * how much of their guess was right, which turns a key worth guessing into one worth
     * guessing character by character.
     */
    private fun org.springframework.http.server.reactive.ServerHttpRequest.presentsAValidKey(): Boolean {
        val presented = headers.getFirst(API_KEY_HEADER) ?: return false
        return MessageDigest.isEqual(presented.toByteArray(), expected)
    }

    private companion object {
        const val API_KEY_HEADER = "X-API-Key"
    }
}

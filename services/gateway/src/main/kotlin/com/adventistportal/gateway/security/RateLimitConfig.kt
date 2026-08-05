package com.adventistportal.gateway.security

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * A blunt ceiling on how much any one address can send, applied to every route.
 *
 * Not the same job as the per-endpoint limits in the user service: those know that
 * logging in deserves a stricter allowance than confirming an e-mail address. This one
 * only knows that nobody should be able to flood the system, and it covers chat,
 * inventory and notification, which had no limit of any kind.
 */
@Configuration
class RateLimitConfig {

    /**
     * The address the request came from, not the connection it arrived on.
     *
     * At the edge those are the same thing — until there is a load balancer in front, at
     * which point keying on the connection would put every user in one bucket. That is
     * exactly the bug this whole change is fixing one layer down, so it is not worth
     * repeating here.
     */
    @Bean
    fun clientAddressKeyResolver() = KeyResolver { exchange: ServerWebExchange ->
        Mono.just(
            exchange.request.headers.getFirst(FORWARDED_FOR)
                ?.substringBefore(',')
                ?.trim()
                ?.takeIf(String::isNotBlank)
                ?: exchange.request.remoteAddress?.address?.hostAddress
                ?: UNKNOWN,
        )
    }

    private companion object {
        const val FORWARDED_FOR = "X-Forwarded-For"

        /** One shared bucket for callers with no address at all, which is the safe side. */
        const val UNKNOWN = "unknown"
    }
}

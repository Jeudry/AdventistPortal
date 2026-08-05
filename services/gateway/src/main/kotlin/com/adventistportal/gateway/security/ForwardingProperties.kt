package com.adventistportal.gateway.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gateway.forwarding")
data class ForwardingProperties(
    /**
     * Whether an inbound `X-Forwarded-For` is believed.
     *
     * Off, because the gateway is the edge: the header is then whatever the caller felt
     * like sending. Turn it on only when something trusted sits in front and is known to
     * overwrite it — a load balancer or an ingress — or the rate limit becomes advisory.
     */
    val trustForwardedFor: Boolean = false,
)

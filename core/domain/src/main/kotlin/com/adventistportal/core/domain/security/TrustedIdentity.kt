package com.adventistportal.core.domain.security

/**
 * The identity the gateway asserts to the services behind it.
 *
 * This is a wire contract between two separately deployed processes, so it is one
 * definition rather than a literal typed twice. A service must only ever trust it on a
 * request that reached it through the gateway — the gateway strips any inbound copy so a
 * client cannot forge one, but that guarantee ends the moment a service port is exposed
 * directly.
 */
object TrustedIdentity {
    const val USER_ID_HEADER = "X-AP-User-Id"
}

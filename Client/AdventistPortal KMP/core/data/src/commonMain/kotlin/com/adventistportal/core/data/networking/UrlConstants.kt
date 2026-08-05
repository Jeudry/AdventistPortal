package com.adventistportal.core.data.networking

import com.adventistportal.core.data.BuildKonfig

/**
 * Every address the client uses, derived from the one the gateway answers on.
 *
 * The host used to be compiled in, which made running against the services on your own
 * machine a source edit. It comes from local.properties now, and the WebSocket scheme is
 * derived rather than written twice — the two could otherwise disagree about whether the
 * connection is encrypted.
 */
object UrlConstants {
    private const val HTTPS = "https://"
    private const val HTTP = "http://"
    private const val WSS = "wss://"
    private const val WS = "ws://"

    private val baseUrl = BuildKonfig.BASE_URL.trimEnd('/')

    val BASE_URL_HTTP = "$baseUrl/api"

    val BASE_URL_WS = baseUrl
        .replace(HTTPS, WSS)
        .replace(HTTP, WS)
        .plus("/ws")
}

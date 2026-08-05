plugins {
    id("adventistportal.spring-boot-app")
}
group = "com.adventistportal.gateway"
version = "0.0.1-SNAPSHOT"
description = "AdventistPortal API gateway"

springBoot {
    mainClass.set("com.adventistportal.gateway.GatewayApplicationKt")
}

/**
 * The reactive gateway, not the servlet one, for a single reason: the WebMVC variant does
 * not proxy WebSockets, and chat's connection is a WebSocket. With it, that handshake had
 * to reach chat directly and authenticate itself — the one hole left in the rule that a
 * token is read in exactly one place.
 *
 * The servlet starter arrives through the shared convention and has to go, or Boot starts
 * Tomcat in servlet mode and the gateway never sees a route.
 */
configurations.all {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
}

dependencies {
    // Tracing config, shared so five services cannot report themselves differently.
    implementation(projects.core.observability)
    implementation(platform(libs.spring.cloud.bom))
    implementation(libs.spring.cloud.starter.gateway.webflux)

    /** Only for JwtService: the token's claim names are one definition, shared, not re-typed here. */
    implementation(projects.core.domain)
    implementation(projects.core.service)

    implementation(libs.spring.boot.starter.security)
    implementation(libs.kotlin.reflect)
}

plugins {
    id("adventistportal.spring-boot-app")
}
group = "com.adventistportal.gateway"
version = "0.0.1-SNAPSHOT"
description = "AdventistPortal API gateway"

springBoot {
    mainClass.set("com.adventistportal.gateway.GatewayApplicationKt")
}

dependencies {
    // Tracing config, shared so five services cannot report themselves differently.
    implementation(projects.core.observability)
    implementation(platform(libs.spring.cloud.bom))
    implementation(libs.spring.cloud.starter.gateway)

    /** Only for JwtService: the token's claim names are one definition, shared, not re-typed here. */
    implementation(projects.core.domain)
    implementation(projects.core.service)

    implementation(libs.spring.boot.starter.security)
    implementation(libs.kotlin.reflect)
}

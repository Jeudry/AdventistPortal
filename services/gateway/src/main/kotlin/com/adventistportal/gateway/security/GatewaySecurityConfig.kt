package com.adventistportal.gateway.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@ConfigurationProperties(prefix = "gateway.cors")
data class GatewayCorsProperties(
    val allowedOrigins: List<String> = emptyList(),
)

/**
 * Spring Security here does CORS and nothing else: authorisation is
 * [IdentityPropagationFilter]'s job, and it runs before this chain so that an
 * unauthenticated request never reaches routing.
 */
@Configuration
@EnableWebFluxSecurity
class GatewaySecurityConfig {

    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http
        .csrf { it.disable() }
        .httpBasic { it.disable() }
        .formLogin { it.disable() }
        .cors { }
        .authorizeExchange { it.anyExchange().permitAll() }
        .build()

    @Bean
    fun corsConfigurationSource(properties: GatewayCorsProperties): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = properties.allowedOrigins
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}

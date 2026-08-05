package com.adventistportal.core.api.security

import jakarta.servlet.DispatcherType
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@ConfigurationProperties(prefix = "adventistportal.security")
data class ServiceSecurityProperties(
    /**
     * Ant patterns this service answers without a caller. Order matters and the first
     * match wins, so a pattern must never be a prefix of a protected sibling: a wildcard
     * over the whole auth prefix would quietly open `change-password` along with it.
     */
    val publicPaths: List<String> = emptyList(),
)

/**
 * The chain every service behind the gateway uses. Authentication happened at the edge;
 * what is left here is deciding which of *this* service's endpoints need a caller at all,
 * which is knowledge only this service has.
 */
@Configuration
@EnableConfigurationProperties(ServiceSecurityProperties::class)
class BehindGatewaySecurityConfig {

    @Bean
    fun filterChain(
        http: HttpSecurity,
        identityFilter: TrustedIdentityFilter,
        properties: ServiceSecurityProperties,
    ): SecurityFilterChain = http
        .csrf { it.disable() }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .authorizeHttpRequests { auth ->
            auth.dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
            properties.publicPaths.forEach { auth.requestMatchers(it).permitAll() }
            auth.anyRequest().authenticated()
        }
        .addFilterBefore(identityFilter, UsernamePasswordAuthenticationFilter::class.java)
        .exceptionHandling { it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) }
        .build()
}

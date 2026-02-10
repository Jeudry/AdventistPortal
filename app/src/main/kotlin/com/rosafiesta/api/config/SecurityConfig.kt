package com.rosafiesta.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

/// <summary>
/// Enables method-level security annotations (e.g. @PreAuthorize) for the application.
/// This configuration is separated from the HTTP security configuration to avoid bean name collisions.
/// </summary>
@Configuration("methodSecurityConfig")
@EnableMethodSecurity
class MethodSecurityConfig
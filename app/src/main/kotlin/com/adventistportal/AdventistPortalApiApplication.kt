package com.adventistportal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

/// <summary>
/// Main Spring Boot application entry for AdventistPortal.
/// Placing this class under `com.adventistportal` ensures component scanning,
/// entity scanning and repository detection cover all submodules that
/// use the `com.adventistportal.*` package prefix.
/// </summary>
@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = ["com.adventistportal"])
@EntityScan(basePackages = ["com.adventistportal"])
class AdventistPortalApiApplication

/// <summary>
/// Application entry point.
/// </summary>
fun main(args: Array<String>) {
  runApplication<AdventistPortalApiApplication>(*args)
}
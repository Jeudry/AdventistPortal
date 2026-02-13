package com.rosafiesta

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

/// <summary>
/// Main Spring Boot application entry for RosaFiesta.
/// Placing this class under `com.rosafiesta` ensures component scanning,
/// entity scanning and repository detection cover all submodules that
/// use the `com.rosafiesta.*` package prefix.
/// </summary>
@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = ["com.rosafiesta"])
@EntityScan(basePackages = ["com.rosafiesta"])
class RosaFiestaApiApplication

/// <summary>
/// Application entry point.
/// </summary>
fun main(args: Array<String>) {
  runApplication<RosaFiestaApiApplication>(*args)
}
package com.adventistportal.inventory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

/**
 * The asset register on its own.
 *
 * It owns the inventory_service schema and nothing else. Entities and repositories are
 * found under this class's own package, which is what @SpringBootApplication already
 * registers — naming them again said the default out loud and nothing more.
 */
@SpringBootApplication(scanBasePackages = ["com.adventistportal.inventory", "com.adventistportal.core"])
@EnableMethodSecurity
@EnableScheduling
class InventoryServiceApplication

fun main(args: Array<String>) {
    runApplication<InventoryServiceApplication>(*args)
}

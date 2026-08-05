package com.adventistportal.inventory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

/**
 * The asset register on its own.
 *
 * It owns the inventory_service schema and nothing else: the scans are narrowed to its
 * own packages plus core, so it cannot pick up another service's entities and start
 * managing tables that are not its own.
 */
@SpringBootApplication(scanBasePackages = ["com.adventistportal.inventory", "com.adventistportal.core"])
@EnableMethodSecurity
@EnableScheduling
@EnableJpaRepositories(basePackages = ["com.adventistportal.inventory"])
@EntityScan(basePackages = ["com.adventistportal.inventory"])
class InventoryServiceApplication

fun main(args: Array<String>) {
    runApplication<InventoryServiceApplication>(*args)
}

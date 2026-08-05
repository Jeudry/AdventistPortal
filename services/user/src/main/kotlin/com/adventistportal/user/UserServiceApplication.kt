package com.adventistportal.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Authentication and accounts on their own.
 *
 * It owns the user_service schema and nothing else: the scans are narrowed to its own
 * packages plus core, so it cannot accidentally pick up another service's entities and
 * start managing tables that are not its own.
 */
@SpringBootApplication(scanBasePackages = ["com.adventistportal.user", "com.adventistportal.core"])
@EnableScheduling
@EnableJpaRepositories(basePackages = ["com.adventistportal.user"])
@EntityScan(basePackages = ["com.adventistportal.user"])
class UserServiceApplication

fun main(args: Array<String>) {
    runApplication<UserServiceApplication>(*args)
}

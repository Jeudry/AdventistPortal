package com.adventistportal.notification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * The notification service on its own.
 *
 * It owns the notification_service schema and nothing else: the scans are narrowed to
 * its own packages plus core, so it cannot accidentally pick up another service's
 * entities and start managing tables that are not its own.
 */
@SpringBootApplication(scanBasePackages = ["com.adventistportal.notification", "com.adventistportal.core"])
@EnableScheduling
@EnableJpaRepositories(basePackages = ["com.adventistportal.notification"])
@EntityScan(basePackages = ["com.adventistportal.notification"])
class NotificationServiceApplication

fun main(args: Array<String>) {
    runApplication<NotificationServiceApplication>(*args)
}

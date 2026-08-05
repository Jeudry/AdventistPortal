package com.adventistportal.chat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Chat on its own.
 *
 * It owns the chat_service schema and nothing else: the scans are narrowed to its own
 * packages plus core, so it cannot pick up another service's entities and start managing
 * tables that are not its own.
 */
@SpringBootApplication(scanBasePackages = ["com.adventistportal.chat", "com.adventistportal.core"])
@EnableScheduling
@EnableJpaRepositories(basePackages = ["com.adventistportal.chat"])
@EntityScan(basePackages = ["com.adventistportal.chat"])
class ChatServiceApplication

fun main(args: Array<String>) {
    runApplication<ChatServiceApplication>(*args)
}

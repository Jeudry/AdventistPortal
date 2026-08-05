package com.adventistportal.core.infrastructure.message_queue.outbox

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "adventistportal.outbox")
data class OutboxProperties(
    /** The schema this service owns; the outbox lives beside the tables it commits with. */
    val schema: String,
    val batchSize: Int = 100,
)

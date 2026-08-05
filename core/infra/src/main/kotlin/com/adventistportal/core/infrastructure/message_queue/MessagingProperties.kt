package com.adventistportal.core.infrastructure.message_queue

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * What this service listens to.
 *
 * Queue names used to be shared Kotlin constants, which meant every service declared
 * every other service's queues and a rename recompiled all of them. Worse, two sides
 * disagreeing on a name fails silently: the queue exists, the binding exists, and the
 * message simply never arrives.
 *
 * A queue is the consumer's own mailbox, so it belongs to the consumer's configuration.
 * The exchange and routing key stay with the event, because those are the contract the
 * publisher promises.
 */
@ConfigurationProperties(prefix = "adventistportal.messaging")
data class MessagingProperties(
    /**
     * Exchanges this service publishes to. Declared even with no subscriber, because
     * publishing to an exchange that does not exist is an error on the channel.
     */
    val publishesTo: List<String> = emptyList(),
    /**
     * The queue names, by short key. `@RabbitListener` needs a placeholder it can resolve
     * at annotation level, so this is where a name is written; [subscriptions] refers back
     * to these rather than repeating the literal.
     */
    val queues: Map<String, String> = emptyMap(),
    val subscriptions: List<Subscription> = emptyList(),
) {
    data class Subscription(
        val queue: String,
        val exchange: String,
        /** Topic pattern, e.g. `user.*` or a single key like `chat.new_message`. */
        val routingKey: String,
    )
}

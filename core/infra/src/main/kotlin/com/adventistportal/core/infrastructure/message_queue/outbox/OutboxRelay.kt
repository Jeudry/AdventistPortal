package com.adventistportal.core.infrastructure.message_queue.outbox

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import com.adventistportal.core.infrastructure.message_queue.proto.ProtoWire
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Moves committed events onto the bus.
 *
 * Delivery is at-least-once: a send that succeeds and then fails to be marked is sent
 * again. Consumers have to tolerate seeing an event twice — which is the trade the outbox
 * makes, and a far better one than the alternative it replaces, where a failed publish
 * left the database saying something the rest of the system was never told.
 */
@Component
@EnableConfigurationProperties(OutboxProperties::class)
class OutboxRelay(
    private val rabbitTemplate: RabbitTemplate,
    private val store: OutboxStore,
    private val properties: OutboxProperties,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelayString = "\${adventistportal.outbox.poll-interval-ms:1000}")
    @Transactional
    fun drain() {
        val claimed = store.claimUnsent(properties.batchSize)
        if (claimed.isEmpty()) return

        val sent = claimed.filter(::send)
        store.markSent(sent.map { it.id })

        if (sent.size < claimed.size) {
            logger.warn("Sent ${sent.size} of ${claimed.size} outbox records; the rest stay for the next pass")
        }
    }

    private fun send(record: OutboxRecord): Boolean = runCatching {
        rabbitTemplate.send(record.exchange, record.routingKey, record.asMessage())
    }.onFailure { failure ->
        logger.error("Could not send outbox record ${record.id} (${record.protoType})", failure)
        store.recordFailure(record.id, failure.toString())
    }.isSuccess

    /**
     * Built by hand rather than through the converter: the payload was encoded when the
     * event was committed, and decoding it back into a domain object only to re-encode it
     * would let a later build change what an already-committed event means.
     */
    private fun OutboxRecord.asMessage(): Message {
        val messageProperties = MessageProperties().apply {
            contentType = ProtoWire.CONTENT_TYPE
            setHeader(ProtoWire.TYPE_HEADER, protoType)
        }
        return Message(payload, messageProperties)
    }
}

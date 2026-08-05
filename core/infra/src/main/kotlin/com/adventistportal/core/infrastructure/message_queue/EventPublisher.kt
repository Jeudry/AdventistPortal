package com.adventistportal.core.infrastructure.message_queue

import com.adventistportal.core.domain.events.AdventistPortalEvent
import com.adventistportal.core.infrastructure.message_queue.outbox.OutboxRecord
import com.adventistportal.core.infrastructure.message_queue.outbox.OutboxStore
import com.adventistportal.core.infrastructure.message_queue.proto.EventProtoMapper
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Records an event as part of the transaction that caused it.
 *
 * Nothing reaches the broker here; the relay does that once the transaction has
 * committed. Sending inside the transaction was the bug this replaces: the send could
 * fail after the commit, the failure was swallowed, and a user would exist that no other
 * service was ever told about.
 *
 * Failures are deliberately not caught. If the event cannot be recorded, the change that
 * produced it must not stand either.
 */
@Component
class EventPublisher(private val outbox: OutboxStore) {

    fun <T : AdventistPortalEvent> publish(event: T) {
        outbox.append(
            OutboxRecord(
                id = UUID.randomUUID(),
                exchange = event.exchange,
                routingKey = event.eventKey,
                protoType = EventProtoMapper.protoTypeOf(event),
                payload = EventProtoMapper.toBytes(event),
            ),
        )
    }
}

package com.adventistportal.core.infrastructure.message_queue

import com.adventistportal.core.domain.events.AdventistPortalEvent
import com.adventistportal.core.infrastructure.message_queue.outbox.OutboxRecord
import com.adventistportal.core.infrastructure.message_queue.outbox.OutboxStore
import com.adventistportal.core.infrastructure.message_queue.proto.EventProtoMapper
import io.micrometer.tracing.Tracer
import io.micrometer.tracing.propagation.Propagator
import org.springframework.beans.factory.ObjectProvider
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
class EventPublisher(
    private val outbox: OutboxStore,
    /** Optional: a service with no tracing configured still has to publish its events. */
    private val propagator: ObjectProvider<Propagator>,
    private val tracer: ObjectProvider<Tracer>,
) {

    fun <T : AdventistPortalEvent> publish(event: T) {
        outbox.append(
            OutboxRecord(
                id = UUID.randomUUID(),
                exchange = event.exchange,
                routingKey = event.eventKey,
                protoType = EventProtoMapper.protoTypeOf(event),
                payload = EventProtoMapper.toBytes(event),
                traceParent = currentTraceParent(),
            ),
        )
    }

    /**
     * Captured here rather than in the relay: this is the request that caused the event,
     * and by the time the relay runs it is long gone.
     */
    private fun currentTraceParent(): String? {
        val context = tracer.ifAvailable?.currentSpan()?.context() ?: return null
        val inject = propagator.ifAvailable ?: return null

        val headers = mutableMapOf<String, String>()
        inject.inject(context, headers) { carrier, key, value -> carrier?.put(key, value) }
        return headers[TRACE_PARENT]
    }

    private companion object {
        const val TRACE_PARENT = "traceparent"
    }
}

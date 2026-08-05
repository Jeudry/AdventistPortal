package com.adventistportal.core.infrastructure.message_queue.outbox

import java.util.UUID

/**
 * An event that has been committed but not yet put on the bus.
 *
 * It carries the encoded payload rather than the domain object: the relay that sends it
 * runs long after the transaction that wrote it, possibly in a build where the sealed
 * class has moved. Bytes plus a proto type is the same contract the wire uses.
 */
data class OutboxRecord(
    val id: UUID,
    val exchange: String,
    val routingKey: String,
    val protoType: String,
    val payload: ByteArray,
    /**
     * The W3C trace context of the request that produced this event, replayed when the
     * relay sends it. Without it the trace stops at the commit: the relay runs on its own
     * schedule, so what reaches the consumer would look like work nobody asked for.
     */
    val traceParent: String? = null,
) {
    override fun equals(other: Any?) = this === other || (other is OutboxRecord && id == other.id)
    override fun hashCode() = id.hashCode()
}

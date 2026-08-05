package com.adventistportal.core.infrastructure.message_queue.proto

/**
 * How a protobuf event is labelled on the bus. Read by every consumer and written by both
 * the converter and the outbox relay, so it is one definition.
 */
object ProtoWire {
    const val CONTENT_TYPE = "application/x-protobuf"
    const val TYPE_HEADER = "proto-type"
}

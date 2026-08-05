package com.adventistportal.core.infrastructure.message_queue.proto

import com.adventistportal.core.domain.events.AdventistPortalEvent
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.support.converter.MessageConversionException
import org.springframework.amqp.support.converter.MessageConverter

/**
 * Puts the events on the wire as protobuf instead of JSON.
 *
 * The previous converter embedded the Kotlin class name in the payload, which made the
 * contract "a sealed class at this exact package". Renaming a package broke every
 * message in flight. What travels now is the contract in contracts/proto, versioned and
 * checked by `buf breaking`, and the class names are free to move.
 */
class ProtoMessageConverter : MessageConverter {

    override fun toMessage(objectToConvert: Any, messageProperties: MessageProperties): Message {
        if (objectToConvert !is AdventistPortalEvent) {
            throw MessageConversionException("Only domain events go on this bus, got ${objectToConvert::class.qualifiedName}")
        }
        messageProperties.contentType = ProtoWire.CONTENT_TYPE
        messageProperties.setHeader(ProtoWire.TYPE_HEADER, EventProtoMapper.protoTypeOf(objectToConvert))
        return Message(EventProtoMapper.toBytes(objectToConvert), messageProperties)
    }

    override fun fromMessage(message: Message): Any {
        val protoType = message.messageProperties.getHeader<String>(ProtoWire.TYPE_HEADER)
            ?: throw MessageConversionException("Message carries no ${ProtoWire.TYPE_HEADER} header")

        return runCatching { EventProtoMapper.fromBytes(protoType, message.body) }
            .getOrElse { throw MessageConversionException("Could not read $protoType off the wire", it) }
    }
}

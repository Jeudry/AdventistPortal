package com.adventistportal.chat.api.websocket

import com.adventistportal.chat.api.dto.ws.OutgoingWebsocketMessage
import com.adventistportal.core.domain.types.UserId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.springframework.amqp.core.AnonymousQueue
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * A push on its way to whichever instance is holding the socket.
 *
 * The handler keeps its connections in maps inside the process, which is the only place
 * they can live — a socket is a file descriptor, not a row. What cannot stay local is
 * *delivery*: with two instances, sending to a local map reaches the users who happen to
 * be connected to you and silently misses the rest, so half a chat sees the message.
 *
 * Every push goes to a fanout instead, and every instance delivers to the sockets it has.
 * The one holding the recipient sends it; the others find nothing and do nothing.
 */
@Serializable
data class SocketPush(
    @Contextual val userId: UserId,
    val message: OutgoingWebsocketMessage,
)

@Configuration
class SocketFanoutConfig {

    @Bean
    fun socketFanoutExchange() = FanoutExchange(SOCKET_FANOUT, true, false)

    /**
     * One queue per running instance, deleted when it disconnects. A durable shared queue
     * would be wrong in the way that matters: the broker would hand each push to *one*
     * consumer, and the odds of it being the instance holding that socket fall as you add
     * instances.
     */
    @Bean
    fun socketFanoutQueue(): Queue = AnonymousQueue()

    @Bean
    fun socketFanoutBinding(socketFanoutQueue: Queue, socketFanoutExchange: FanoutExchange): Binding =
        BindingBuilder.bind(socketFanoutQueue).to(socketFanoutExchange)

    /**
     * Its own listener factory, because the shared one converts every message through the
     * protobuf converter and a socket push is not a domain event — it arrived as
     * "Message carries no proto-type header", several layers from anything that looked
     * related.
     *
     * No retries and no dead-letter queue either, and that is the right trade here: a push
     * is only worth delivering to a socket that is open now. Keeping one to try again
     * later would deliver yesterday's message to a connection that has moved on.
     */
    @Bean
    fun socketListenerFactory(connectionFactory: ConnectionFactory): SimpleRabbitListenerContainerFactory =
        SimpleRabbitListenerContainerFactory().apply {
            setConnectionFactory(connectionFactory)
            setObservationEnabled(true)
        }

    companion object {
        const val SOCKET_FANOUT = "chat.sockets"
    }
}

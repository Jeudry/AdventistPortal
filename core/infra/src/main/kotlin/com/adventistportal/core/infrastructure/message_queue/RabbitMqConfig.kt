package com.adventistportal.core.infrastructure.message_queue

import com.adventistportal.core.infrastructure.message_queue.proto.ProtoMessageConverter
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Declarables
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(MessagingProperties::class)
class RabbitMqConfig {

    @Bean
    fun messageConverter(): MessageConverter = ProtoMessageConverter()

    @Bean
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        messageConverter: MessageConverter,
    ): RabbitTemplate = RabbitTemplate(connectionFactory).apply {
        this.messageConverter = messageConverter
        // Set here rather than through spring.rabbitmq.template.observation-enabled: that
        // property configures the template Boot builds, and this one is ours.
        setObservationEnabled(true)
    }

    @Bean
    fun rabbitListenerContainerFactory(
        connectionFactory: ConnectionFactory,
        transactionManager: PlatformTransactionManager,
        messageConverter: MessageConverter,
    ): SimpleRabbitListenerContainerFactory = SimpleRabbitListenerContainerFactory().apply {
        setTransactionManager(transactionManager)
        setConnectionFactory(connectionFactory)
        setChannelTransacted(true)
        setMessageConverter(messageConverter)
        // Without this the consumer discards the trace context on the message, and every
        // event handled looks like work nobody asked for.
        setObservationEnabled(true)

        // A handler that throws must not get the message back forever. It is retried a few
        // times for the sake of a broker or an SMTP server having a bad second, and then
        // rejected — which, with requeue off, means the queue's dead-letter address.
        setDefaultRequeueRejected(false)
        setAdviceChain(
            RetryInterceptorBuilder.stateless()
                .maxRetries(RETRIES_BEFORE_DEAD_LETTERING)
                .backOffOptions(INITIAL_BACKOFF_MS, BACKOFF_MULTIPLIER, MAX_BACKOFF_MS)
                .recoverer(RejectAndDontRequeueRecoverer())
                .build(),
        )
    }

    /**
     * Only what this service subscribes to, built from its own configuration. Previously
     * every service declared every other service's queues, so a queue existed whether or
     * not anything was reading it.
     *
     * Exchanges are declared here too: declaring one is idempotent, and a consumer that
     * starts before its publisher still needs something to bind to.
     */
    @Bean
    fun messagingTopology(properties: MessagingProperties): Declarables {
        val exchanges = (properties.publishesTo + properties.subscriptions.map { it.exchange })
            .distinct()
            .associateWith { TopicExchange(it, true, false) }

        // Each queue dead-letters through the default exchange, which routes by queue
        // name — so the pair needs no exchange of its own. Both arguments are required:
        // RabbitMQ rejects a routing key with no exchange to send it through.
        val queues = properties.subscriptions.associate {
            it.queue to QueueBuilder.durable(it.queue)
                .deadLetterExchange(DEFAULT_EXCHANGE)
                .deadLetterRoutingKey(it.queue.deadLetterName())
                .build()
        }

        val deadLetterQueues = properties.subscriptions.map { Queue(it.queue.deadLetterName(), true) }

        val bindings = properties.subscriptions.map { subscription ->
            BindingBuilder
                .bind(queues.getValue(subscription.queue))
                .to(exchanges.getValue(subscription.exchange))
                .with(subscription.routingKey)
        }

        return Declarables(exchanges.values + queues.values + deadLetterQueues + bindings)
    }

    /**
     * Where a message goes once it has failed every attempt. It is kept, not dropped: an
     * event that cannot be handled is something to go and look at, and a queue that is
     * empty for the wrong reason looks exactly like one that is empty for the right one.
     */
    private fun String.deadLetterName() = "$this$DEAD_LETTER_SUFFIX"

    private companion object {
        const val DEFAULT_EXCHANGE = ""
        const val DEAD_LETTER_SUFFIX = ".dlq"
        /** Retries, not attempts: the first delivery is not one of these. */
        const val RETRIES_BEFORE_DEAD_LETTERING = 2
        const val INITIAL_BACKOFF_MS = 1_000L
        const val MAX_BACKOFF_MS = 10_000L
        const val BACKOFF_MULTIPLIER = 2.0
    }
}

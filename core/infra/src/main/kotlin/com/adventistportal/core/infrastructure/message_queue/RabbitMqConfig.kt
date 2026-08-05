package com.adventistportal.core.infrastructure.message_queue

import com.adventistportal.core.infrastructure.message_queue.proto.ProtoMessageConverter
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Declarables
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
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

        val queues = properties.subscriptions.associate { it.queue to Queue(it.queue, true) }

        val bindings = properties.subscriptions.map { subscription ->
            BindingBuilder
                .bind(queues.getValue(subscription.queue))
                .to(exchanges.getValue(subscription.exchange))
                .with(subscription.routingKey)
        }

        return Declarables(exchanges.values + queues.values + bindings)
    }
}

@file:Suppress("DEPRECATION")

package com.adventistportal.core.infrastructure.message_queue

import com.adventistportal.core.domain.events.chat.ChatEventConstants
import com.adventistportal.core.domain.events.user.UserEventConstants
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import com.adventistportal.core.infrastructure.message_queue.proto.ProtoMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
class RabbitMqConfig {
    @Bean
    fun messageConverter(): MessageConverter = ProtoMessageConverter()

    @Bean
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        messageConverter: MessageConverter,
    ): RabbitTemplate {
        return RabbitTemplate(connectionFactory).apply {
            this.messageConverter = messageConverter
        }
    }

    @Bean
    fun rabbitListenerContainerFactory(
        connectionFactory: ConnectionFactory,
        transactionManager: PlatformTransactionManager,
        messageConverter: MessageConverter
    ): SimpleRabbitListenerContainerFactory {
        return SimpleRabbitListenerContainerFactory().apply {
            this.setTransactionManager(transactionManager)
            this.setConnectionFactory(connectionFactory)
            this.setChannelTransacted(true)
            this.setMessageConverter(messageConverter)
        }
    }

    @Bean
    fun userExchange() = TopicExchange(
        UserEventConstants.USER_EXCHANGE,
        true,
        false
    )

    @Bean
    fun chatExchange() = TopicExchange(
        ChatEventConstants.CHAT_EXCHANGE,
        true,
        false
    )


    @Bean
    fun chatUserEventsQueue() = Queue(
        MessageQueues.CHAT_USER_EVENTS,
        true
    )

    @Bean
    fun notificationUserEventsQueue() = Queue(
        MessageQueues.NOTIFICATION_USER_EVENTS,
        true
    )
  
  @Bean
  fun notificationChatEventsQueue() = Queue(
    MessageQueues.NOTIFICATION_CHAT_EVENTS,
    true
  )

    @Bean
    fun chatUserBinding(
        chatUserEventsQueue: Queue,
        userExchange: TopicExchange
    ): Binding {
        return BindingBuilder
            .bind(chatUserEventsQueue)
            .to(userExchange)
            .with("user.*")
    }

    @Bean
    fun notificationUserBinding(
        notificationUserEventsQueue: Queue,
        userExchange: TopicExchange
    ): Binding {
        return BindingBuilder
            .bind(notificationUserEventsQueue)
            .to(userExchange)
            .with("user.*")
    }
  
  @Bean
  fun notificationChatBinding(
    notificationChatEventsQueue: Queue,
    chatExchange: TopicExchange
  ): Binding {
    return BindingBuilder
      .bind(notificationChatEventsQueue)
      .to(chatExchange)
      .with(ChatEventConstants.CHAT_NEW_MESSAGE)
  }
}
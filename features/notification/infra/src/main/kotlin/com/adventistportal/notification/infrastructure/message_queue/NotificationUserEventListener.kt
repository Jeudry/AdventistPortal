package com.adventistportal.notification.infrastructure.message_queue

import com.adventistportal.core.domain.events.user.UserEvent
import com.adventistportal.core.infrastructure.message_queue.MessageQueues
import com.adventistportal.notification.infrastructure.service.EmailService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Component
class NotificationUserEventListener(private val emailService: EmailService) {

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_USER_EVENTS])
    @Transactional
    fun handleUserEvent(event: UserEvent){
        when(event){
            is UserEvent.RegistrationStarted -> {
                emailService.sendVerificationEmail(
                    event.email,
                    event.username,
                    event.registrationId,
                    event.verificationToken
                )
            }
            is UserEvent.RequestResendVerification ->{
                emailService.sendVerificationEmail(
                    event.email,
                    event.username,
                    event.registrationId,
                    event.verificationToken
                )
            }
            is UserEvent.RequestResetPassword -> {
                emailService.sendPasswordResetEmail(
                    event.email,
                    event.username,
                    event.userId,
                    event.verificationToken,
                    Duration.ofMinutes(event.expiresInMinutes)
                )
            }
            else -> Unit
        }
    }
}
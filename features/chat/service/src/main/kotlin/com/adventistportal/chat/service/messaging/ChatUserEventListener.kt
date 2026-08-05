package com.adventistportal.chat.service.messaging

import com.adventistportal.core.domain.events.user.UserEvent
import com.adventistportal.chat.domain.models.ChatParticipant
import com.adventistportal.chat.service.ChatParticipantService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class ChatUserEventListener(
    private val chatParticipantService: ChatParticipantService
){
    private val logger = org.slf4j.LoggerFactory.getLogger(ChatUserEventListener::class.java)

    @RabbitListener(queues = ["\${adventistportal.messaging.queues.user-events}"])
    fun handleUserEvent(event: UserEvent){
            when(event){
                is UserEvent.Created -> {
                    chatParticipantService.createChatParticipant(
                        chatParticipant = ChatParticipant(
                            userId = event.userId,
                            username = event.username,
                            email = event.email,
                            profilePictureUrl = null
                        )
                    )
                    logger.info("Chat participant created for new user: ${event.userId}")
                }
                else -> Unit
            }
    }
}
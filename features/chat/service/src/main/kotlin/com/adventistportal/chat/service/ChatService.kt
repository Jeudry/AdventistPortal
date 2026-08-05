package com.adventistportal.chat.service

import com.adventistportal.chat.domain.events.ChatCreatedEvent
import com.adventistportal.chat.domain.events.ChatParticipantLeftEvent
import com.adventistportal.chat.domain.events.ChatParticipantsJoinedEvent
import com.adventistportal.chat.domain.exceptions.ChatNotFoundEx
import com.adventistportal.chat.domain.exceptions.ChatParticipantNotFoundEx
import com.adventistportal.chat.domain.exceptions.InvalidChatSizeEx
import com.adventistportal.chat.domain.exceptions.SelfInvitationNotAllowedEx
import com.adventistportal.core.domain.exceptions.*
import com.adventistportal.chat.domain.models.Chat
import com.adventistportal.chat.domain.models.ChatMessage
import com.adventistportal.core.domain.types.ChatId
import com.adventistportal.core.domain.types.UserId
import com.adventistportal.chat.infra.database.entities.ChatEntity
import com.adventistportal.chat.infra.database.entities.ChatParticipantEntity
import com.adventistportal.chat.infra.database.mappers.toModel
import com.adventistportal.chat.infra.database.repositories.ChatMessageRepository
import com.adventistportal.chat.infra.users.MissingParticipantRepair
import com.adventistportal.chat.infra.database.repositories.ChatParticipantRepository
import com.adventistportal.chat.infra.database.repositories.ChatRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val missingParticipantRepair: MissingParticipantRepair,
) {
    @Cacheable(
        value = ["messages"],
        key = "#chatId",
        condition = "#before == null && #pageSize <= 50",
        sync = true
    )
    fun getChatMessages(
        chatId: ChatId,
        before: Instant,
        pageSize: Int
    ): List<ChatMessage> {
        return chatMessageRepository.findByChatIdBefore(
            chatId = chatId,
            before = before,
            pageable = PageRequest.of(0, pageSize)
        ).content
            .asReversed()
            .map { it.toModel() }
    }


    fun getChatById(chatId: ChatId, userId: UserId): Chat?{
        return chatRepository.findChatById(chatId, userId)
            ?.toModel(lastMessageForChat(chatId))
    }

    /**
     * Who should be told about something happening in this chat.
     *
     * A fact about the chat, not about who is connected: with more than one instance the
     * sockets are spread across processes, so asking the local ones would name whoever
     * happens to share a process with the sender.
     */
    @Transactional(readOnly = true)
    fun participantsOf(chatId: ChatId): Set<UserId> =
        chatRepository.findByIdOrNull(chatId)
            ?.participants
            ?.mapNotNull { it.userId }
            ?.toSet()
            .orEmpty()

    fun findChatsByUser(userId: UserId): List<Chat> {
        val chatEntities = chatRepository.findAllByUserId(userId)
        val chatIds = chatEntities.mapNotNull { it.id }
        val latestMessages = chatMessageRepository
            .findLatestMessagesByChatIds(chatIds.toSet())
            .associateBy { it.chatId }

        return chatEntities
            .map {
                it.toModel(
                    lastMessage = latestMessages[it.id]?.toModel()
                )
            }.sortedByDescending { it.lastActivityAt }
    }

    @Transactional
    /**
     * A participant this service has never heard of is not necessarily a participant that
     * does not exist: the event that would have created it may never have arrived. Ask
     * before refusing — otherwise the hole in the projection is permanent.
     */
    private fun recoverParticipant(userId: UserId): ChatParticipantEntity {
        val recovered = missingParticipantRepair.lookUp(userId) ?: throw ChatParticipantNotFoundEx(userId)

        return chatParticipantRepository.saveAndFlush(
            ChatParticipantEntity(
                userId = recovered.userId,
                username = recovered.username,
                email = recovered.email,
                profilePictureUrl = recovered.profilePictureUrl,
            ),
        )
    }

    fun createChat(
        creatorId: UserId,
        otherUsersId: Set<UserId>
    ): Chat {
        if (otherUsersId.contains(creatorId)) {
            throw SelfInvitationNotAllowedEx(creatorId)
        }

        val otherParticipants = chatParticipantRepository.findByUserIdIn(
            otherUsersId.toList()
        )

        val allParticipants = (otherParticipants + creatorId)
        if(allParticipants.size < 2){
            throw InvalidChatSizeEx()
        }

        val creator = chatParticipantRepository.findByIdOrNull(creatorId)
            ?: recoverParticipant(creatorId)

        return chatRepository.saveAndFlush(
            ChatEntity(
                creator = creator,
                participants = setOf(creator) + otherParticipants
            )
        ).toModel().also { entity ->
          applicationEventPublisher.publishEvent(
            ChatCreatedEvent(
                    chatId = entity.id,
                    participantIds = entity.participants.map { it.userId }
                )
            )
        }
    }

    @Transactional
    fun addParticipantsToChat(
        requestUserId: UserId,
        chatId: ChatId,
        userIds: Set<UserId>
    ): Chat {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundEx()

        val isRequestingUserInTheChat = chat.participants.any {
            it.userId == requestUserId
        }

        if (!isRequestingUserInTheChat) {
            throw ForbiddenEx()
        }

        val users = userIds.map { userId ->
            chatParticipantRepository.findByIdOrNull(userId)
                ?: throw ChatParticipantNotFoundEx(userId)
        }

        val lastMessage = lastMessageForChat(chatId)
        val updatedChat = chatRepository.save(
            chat.apply {
                this.participants = chat.participants + users
            }
        ).toModel(lastMessage)

        applicationEventPublisher.publishEvent(
            ChatParticipantsJoinedEvent(
                chatId = chatId,
                usersId = userIds
            )
        )

        return updatedChat
    }

    @Transactional
    fun removeParticipantFromChat(
        chatId: ChatId,
        userId: UserId,
    ) {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundEx()

        val participant = chat.participants.find { it.userId == userId }
            ?: throw ChatParticipantNotFoundEx(userId)

        val newParticipant = chat.participants.size - 1

        if(newParticipant == 0){
            chatRepository.delete(chat)
            return
        }

        chatRepository.save(
            chat.apply {
                this.participants = chat.participants - participant
            }
        )

        applicationEventPublisher.publishEvent(
            ChatParticipantLeftEvent(
                chatId = chatId,
                userId = userId
            )
        )
    }

    private fun lastMessageForChat(chatId: ChatId): ChatMessage? {
        return chatMessageRepository.findLatestMessagesByChatIds(setOf(chatId))
            .firstOrNull()?.toModel()
    }
}
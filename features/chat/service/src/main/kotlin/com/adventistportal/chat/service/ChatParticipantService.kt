package com.adventistportal.chat.service

import com.adventistportal.chat.domain.models.ChatParticipant
import com.adventistportal.core.domain.types.UserId
import com.adventistportal.chat.infra.database.entities.ChatParticipantEntity
import com.adventistportal.chat.infra.database.mappers.toModel
import com.adventistportal.chat.infra.database.repositories.ChatParticipantRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ChatParticipantService(
    private val chatParticipantRepository: ChatParticipantRepository
) {
    fun createChatParticipant(
        chatParticipant: ChatParticipant
    ): ChatParticipant {
        val savedChatParticipant = chatParticipantRepository.saveAndFlush(
            ChatParticipantEntity(
                userId = chatParticipant.userId,
                username = chatParticipant.username,
                email = chatParticipant.email,
                profilePictureUrl = chatParticipant.profilePictureUrl
            )
        )
        return savedChatParticipant.toModel()
    }

    fun findChatParticipantById(userId: UserId): ChatParticipant? {
        return chatParticipantRepository.findByIdOrNull(userId)?.toModel()
    }

    fun findChatParticipantByEmailOrUsername(
        query: String
    ): ChatParticipant? {
        var normalizedQuery = query.lowercase().trim()
        return chatParticipantRepository.findByEmailOrUsername(normalizedQuery)?.toModel()
    }
}
package com.adventistportal.chat.data.mappers

import com.adventistportal.chat.data.dto.ChatParticipantDto
import com.adventistportal.chat.database.entities.ChatParticipantEntity
import com.adventistportal.chat.domain.models.ChatParticipant
import com.adventistportal.core.domain.auth.User

fun ChatParticipantDto.toDomain(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}

fun ChatParticipantEntity.toDomain(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}

fun ChatParticipant.toEntity(): ChatParticipantEntity {
    return ChatParticipantEntity(
        userId = userId,
        username = username,
        profilePictureUrl = profilePictureUrl
    )
}
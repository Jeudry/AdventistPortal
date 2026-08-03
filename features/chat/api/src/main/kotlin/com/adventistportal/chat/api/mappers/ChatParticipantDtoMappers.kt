package com.adventistportal.chat.api.mappers

import com.adventistportal.chat.api.dto.ChatParticipantDto
import com.adventistportal.chat.domain.models.ChatParticipant

fun ChatParticipant.toDto(): ChatParticipantDto {
    return ChatParticipantDto(
        id = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl
    )
}
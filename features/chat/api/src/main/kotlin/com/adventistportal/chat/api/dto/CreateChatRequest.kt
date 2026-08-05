package com.adventistportal.chat.api.dto

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.UserId
import jakarta.validation.constraints.Size

@Serializable
data class CreateChatRequest(
    @field:Size(min = 1, message = "Chat must have at least two participants.")
    val otherUsersId: List<@Contextual UserId>
){

}

package com.adventistportal.chat.api.dto.ws

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.UserId

@Serializable
data class ProfilePictureUpdateDto(
  @Contextual val userId: UserId,
  val newUrl: String? = null
  )
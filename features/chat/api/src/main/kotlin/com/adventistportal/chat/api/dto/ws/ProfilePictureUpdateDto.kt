package com.adventistportal.chat.api.dto.ws

import com.adventistportal.core.domain.types.UserId

data class ProfilePictureUpdateDto(
  val userId: UserId,
  val newUrl: String? = null
  )
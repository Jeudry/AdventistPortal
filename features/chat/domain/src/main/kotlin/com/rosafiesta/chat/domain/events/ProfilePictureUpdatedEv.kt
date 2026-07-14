package com.adventistportal.chat.domain.events

import com.adventistportal.core.domain.types.UserId

data class ProfilePictureUpdatedEv(
  val userId: UserId,
  val newUrl: String? = null
)
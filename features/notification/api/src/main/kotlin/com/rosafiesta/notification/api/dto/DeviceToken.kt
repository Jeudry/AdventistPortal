package com.adventistportal.notification.api.dto

import com.adventistportal.core.domain.types.UserId
import java.time.Instant

data class DeviceTokenDto(
  val userId: UserId,
  val token: String,
  val createdAt: Instant
)
package com.adventistportal.notification.api.dto

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import com.adventistportal.core.domain.types.UserId
import java.time.Instant

@Serializable
data class DeviceTokenDto(
  @Contextual val userId: UserId,
  val token: String,
  @Contextual val createdAt: Instant
)
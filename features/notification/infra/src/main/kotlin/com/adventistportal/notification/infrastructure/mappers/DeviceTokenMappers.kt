package com.adventistportal.notification.infrastructure.mappers

import com.adventistportal.notification.domain.model.DeviceToken
import com.adventistportal.notification.infrastructure.database.DeviceTokenEntity

fun DeviceTokenEntity.toModel(): DeviceToken {
  return DeviceToken(
    id = id,
    userId = userId,
    token = token,
    platform = platform.toModel(),
    createdAt = createdAt,
  )
}
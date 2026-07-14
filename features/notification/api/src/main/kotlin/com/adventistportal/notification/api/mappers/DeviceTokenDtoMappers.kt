package com.adventistportal.notification.api.mappers

import com.adventistportal.notification.api.dto.DeviceTokenDto
import com.adventistportal.notification.domain.model.DeviceToken

fun DeviceToken.toDto(): DeviceTokenDto {
  return DeviceTokenDto(
    userId = userId,
    token = token,
    createdAt = createdAt
  )
}
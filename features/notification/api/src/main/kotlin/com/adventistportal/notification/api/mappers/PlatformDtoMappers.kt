package com.adventistportal.notification.api.mappers

import com.adventistportal.notification.api.dto.PlatformDto
import com.adventistportal.notification.domain.model.DeviceToken

fun PlatformDto.toDto(): DeviceToken.Platform {
  return when (this) {
    PlatformDto.ANDROID -> DeviceToken.Platform.ANDROID
    PlatformDto.IOS -> DeviceToken.Platform.IOS
    PlatformDto.WEB -> DeviceToken.Platform.WEB
  }
}
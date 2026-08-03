package com.adventistportal.notification.infrastructure.mappers

import com.adventistportal.notification.domain.model.DeviceToken
import com.adventistportal.notification.infrastructure.database.PlatformEntity

fun DeviceToken.Platform.toEntity(): PlatformEntity {
  return when(this) {
    DeviceToken.Platform.ANDROID -> PlatformEntity.ANDROID
    DeviceToken.Platform.IOS -> PlatformEntity.IOS
    DeviceToken.Platform.WEB -> PlatformEntity.WEB
  }
}

fun PlatformEntity.toModel(): DeviceToken.Platform {
  return when(this) {
    PlatformEntity.ANDROID -> DeviceToken.Platform.ANDROID
    PlatformEntity.IOS -> DeviceToken.Platform.IOS
    PlatformEntity.WEB -> DeviceToken.Platform.WEB
  }
}
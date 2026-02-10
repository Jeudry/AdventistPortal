package com.rosafiesta.notification.api.controller

import com.rosafiesta.core.api.utils.requestUserId
import com.rosafiesta.notification.api.dto.DeviceTokenDto
import com.rosafiesta.notification.api.dto.RegisterDeviceRequest
import com.rosafiesta.notification.api.mappers.toDto
import com.rosafiesta.notification.infrastructure.service.PushNotificationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notification")
class DeviceTokenController(private val pushNotificationService: PushNotificationService) {
  
  @PostMapping("/register")
  fun registerDeviceToken(
    @Valid @RequestBody body: RegisterDeviceRequest
  ): DeviceTokenDto {
    return pushNotificationService.registerDevice(
      userId = requestUserId,
      token = body.token,
      platform = body.platform.toDto()
    ).toDto()
  }
  
  @DeleteMapping("/{token}")
  fun unregisterDeviceToken(
    @PathVariable("token") token: String
  ) {
    pushNotificationService.unregisterDevice(
      token = token
    )
  }
  
  
}
package com.adventistportal.notification.api.controller

import com.adventistportal.core.api.utils.requestUserId
import com.adventistportal.notification.api.dto.DeviceTokenDto
import com.adventistportal.notification.api.dto.RegisterDeviceRequest
import com.adventistportal.notification.api.mappers.toDto
import com.adventistportal.notification.infrastructure.service.PushNotificationService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/notification")
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
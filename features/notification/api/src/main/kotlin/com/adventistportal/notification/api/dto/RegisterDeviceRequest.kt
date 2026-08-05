package com.adventistportal.notification.api.dto

import kotlinx.serialization.Serializable

import jakarta.validation.constraints.NotBlank

@Serializable
data class RegisterDeviceRequest(
  @field:NotBlank
  val token: String,
  val platform: PlatformDto
)

enum class PlatformDto {
  ANDROID,
  IOS,
  WEB
}
package com.adventistportal.chat.api.dto

import kotlinx.serialization.Serializable

import jakarta.validation.constraints.NotBlank

@Serializable
data class ConfirmProfilePictureRequest(
  @field:NotBlank
  val publicUrl: String,
)
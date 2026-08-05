package com.adventistportal.chat.api.dto

import kotlinx.serialization.Contextual

import kotlinx.serialization.Serializable

import java.time.Instant

@Serializable
data class ProfilePictureUploadResponse(
  val uploadUrl: String,
  val publicUrl: String,
  val headers: Map<String, String>,
  @Contextual val expiresAt: Instant
)
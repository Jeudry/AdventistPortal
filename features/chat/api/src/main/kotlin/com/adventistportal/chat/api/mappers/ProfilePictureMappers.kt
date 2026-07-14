package com.adventistportal.chat.api.mappers

import com.adventistportal.chat.api.dto.ProfilePictureUploadResponse
import com.adventistportal.chat.domain.models.ProfilePictureUploadCredentials

fun ProfilePictureUploadCredentials.toDto(): ProfilePictureUploadResponse {
  return ProfilePictureUploadResponse(
    uploadUrl = this.uploadUrl,
    publicUrl = this.publicUrl,
    headers = this.headers,
    expiresAt = this.expiresAt
  )
}
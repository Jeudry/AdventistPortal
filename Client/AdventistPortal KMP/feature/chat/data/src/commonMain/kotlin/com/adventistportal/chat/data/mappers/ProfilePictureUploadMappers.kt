package com.adventistportal.chat.data.mappers

import com.adventistportal.chat.data.dto.response.ProfilePictureUploadUrlsResponse
import com.adventistportal.chat.domain.models.ProfilePictureUploadUrls

fun ProfilePictureUploadUrlsResponse.toDomain(): ProfilePictureUploadUrls {
    return ProfilePictureUploadUrls(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers
    )
}
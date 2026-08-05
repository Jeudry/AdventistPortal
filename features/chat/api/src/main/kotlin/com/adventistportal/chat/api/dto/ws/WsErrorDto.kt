package com.adventistportal.chat.api.dto.ws

import kotlinx.serialization.Serializable

@Serializable
data class WsErrorDto(
  val code: String,
  val message: String
)
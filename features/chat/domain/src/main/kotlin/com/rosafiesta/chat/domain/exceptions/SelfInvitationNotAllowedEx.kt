package com.adventistportal.chat.domain.exceptions

import com.adventistportal.core.domain.types.UserId

class SelfInvitationNotAllowedEx(userId: UserId): RuntimeException(
    "User cannot invite themselves to a chat: $userId"
)

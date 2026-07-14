package com.adventistportal.chat.presentation.util

import adventistportal.feature.chat.presentation.generated.resources.Res
import adventistportal.feature.chat.presentation.generated.resources.network_error
import adventistportal.feature.chat.presentation.generated.resources.offline
import adventistportal.feature.chat.presentation.generated.resources.online
import adventistportal.feature.chat.presentation.generated.resources.reconnecting
import adventistportal.feature.chat.presentation.generated.resources.unknown_error
import com.adventistportal.chat.domain.models.ConnectionState
import com.adventistportal.core.presentation.util.UiText

fun ConnectionState.toUiText(): UiText {
    val resource = when(this) {
        ConnectionState.DISCONNECTED -> Res.string.offline
        ConnectionState.CONNECTING -> Res.string.reconnecting
        ConnectionState.CONNECTED -> Res.string.online
        ConnectionState.ERROR_NETWORK -> Res.string.network_error
        ConnectionState.ERROR_UNKNOWN -> Res.string.unknown_error
    }
    return UiText.Resource(resource)
}
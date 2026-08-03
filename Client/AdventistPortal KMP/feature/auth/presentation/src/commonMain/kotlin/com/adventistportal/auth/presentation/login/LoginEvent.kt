package com.adventistportal.auth.presentation.login

sealed interface LoginEvent {
    data object Success: LoginEvent
}
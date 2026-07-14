package com.adventistportal

sealed interface MainEvent {
    data object OnSessionExpired: MainEvent
}
package com.adventistportal.auth.presentation.complete_registration

sealed interface CompleteRegistrationEvent {
    data object Success: CompleteRegistrationEvent
}

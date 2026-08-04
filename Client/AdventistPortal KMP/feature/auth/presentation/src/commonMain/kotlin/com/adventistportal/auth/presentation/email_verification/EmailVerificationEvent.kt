package com.adventistportal.auth.presentation.email_verification

sealed interface EmailVerificationEvent {
    /** Carries the token onward: completing the registration needs it too. */
    data class Verified(val token: String): EmailVerificationEvent
}

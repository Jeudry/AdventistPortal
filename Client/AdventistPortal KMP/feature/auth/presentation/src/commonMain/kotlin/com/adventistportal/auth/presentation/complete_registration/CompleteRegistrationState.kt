package com.adventistportal.auth.presentation.complete_registration

import androidx.compose.foundation.text.input.TextFieldState
import com.adventistportal.core.presentation.util.UiText

data class CompleteRegistrationState(
    val firstNameTextState: TextFieldState = TextFieldState(),
    val lastNameTextState: TextFieldState = TextFieldState(),
    val canSubmit: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitError: UiText? = null,
)

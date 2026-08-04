package com.adventistportal.auth.presentation.complete_registration

sealed interface CompleteRegistrationAction {
    data object OnInputTextFocusGain: CompleteRegistrationAction
    data object OnSubmitClick: CompleteRegistrationAction
}

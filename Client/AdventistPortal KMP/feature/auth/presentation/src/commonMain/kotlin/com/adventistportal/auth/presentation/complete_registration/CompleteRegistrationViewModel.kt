package com.adventistportal.auth.presentation.complete_registration

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.adventistportal.auth.presentation.navigation.AuthGraphRoutes
import com.adventistportal.core.domain.auth.AuthService
import com.adventistportal.core.domain.util.onFailure
import com.adventistportal.core.domain.util.onSuccess
import com.adventistportal.core.presentation.util.UiText
import com.adventistportal.core.presentation.util.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompleteRegistrationViewModel(
    private val authService: AuthService,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val token = savedStateHandle.toRoute<AuthGraphRoutes.CompleteRegistration>().token

    private val eventChannel = Channel<CompleteRegistrationEvent>()
    val events = eventChannel.receiveAsFlow()

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(CompleteRegistrationState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeValidationStates()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CompleteRegistrationState()
        )

    fun onAction(action: CompleteRegistrationAction) {
        when (action) {
            CompleteRegistrationAction.OnInputTextFocusGain -> {
                _state.update { it.copy(submitError = null) }
            }
            CompleteRegistrationAction.OnSubmitClick -> submit()
        }
    }

    private fun observeValidationStates() {
        val firstName = snapshotFlow { state.value.firstNameTextState.text.toString() }
            .map { it.isNotBlank() }
            .distinctUntilChanged()

        val lastName = snapshotFlow { state.value.lastNameTextState.text.toString() }
            .map { it.isNotBlank() }
            .distinctUntilChanged()

        combine(firstName, lastName) { hasFirstName, hasLastName ->
            hasFirstName && hasLastName
        }
            .onEach { canSubmit -> _state.update { it.copy(canSubmit = canSubmit) } }
            .launchIn(viewModelScope)
    }

    private fun submit() {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, submitError = null) }

            authService
                .completeRegistration(
                    token = token,
                    firstName = state.value.firstNameTextState.text.toString().trim(),
                    lastName = state.value.lastNameTextState.text.toString().trim()
                )
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false) }
                    eventChannel.send(CompleteRegistrationEvent.Success)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSubmitting = false, submitError = error.toUiText()) }
                }
        }
    }
}

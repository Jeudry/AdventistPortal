package com.adventistportal.auth.presentation.register

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import adventistportal.feature.auth.presentation.generated.resources.Res
import adventistportal.feature.auth.presentation.generated.resources.email
import adventistportal.feature.auth.presentation.generated.resources.email_placeholder
import adventistportal.feature.auth.presentation.generated.resources.login
import adventistportal.feature.auth.presentation.generated.resources.password
import adventistportal.feature.auth.presentation.generated.resources.password_hint
import adventistportal.feature.auth.presentation.generated.resources.register
import adventistportal.feature.auth.presentation.generated.resources.username
import adventistportal.feature.auth.presentation.generated.resources.username_hint
import adventistportal.feature.auth.presentation.generated.resources.username_placeholder
import adventistportal.feature.auth.presentation.generated.resources.welcome_to_adventistportal
import com.adventistportal.core.designsystem.components.brand.AdventistPortalBrandLogo
import com.adventistportal.core.designsystem.components.buttons.AdventistPortalButton
import com.adventistportal.core.designsystem.components.buttons.AdventistPortalButtonStyle
import com.adventistportal.core.designsystem.components.layouts.AdventistPortalAdaptiveFormLayout
import com.adventistportal.core.designsystem.components.layouts.AdventistPortalSnackbarScaffold
import com.adventistportal.core.designsystem.components.textfields.AdventistPortalPasswordTextField
import com.adventistportal.core.designsystem.components.textfields.AdventistPortalTextField
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme
import com.adventistportal.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterRoot(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is RegisterEvent.Success -> {
                onRegisterSuccess(event.email)
            }
        }
    }

    RegisterScreen(
        state = state,
        onAction = { action ->
            when(action) {
                is RegisterAction.OnLoginClick -> onLoginClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    AdventistPortalSnackbarScaffold(
        snackbarHostState = snackbarHostState
    ) {
        AdventistPortalAdaptiveFormLayout(
            headerText = stringResource(Res.string.welcome_to_adventistportal),
            errorText = state.registrationError?.asString(),
            logo = { AdventistPortalBrandLogo() }
        ) {
            AdventistPortalTextField(
                state = state.usernameTextState,
                placeholder = stringResource(Res.string.username_placeholder),
                title = stringResource(Res.string.username),
                supportingText = state.usernameError?.asString()
                    ?: stringResource(Res.string.username_hint),
                isError = state.usernameError != null,
                onFocusChanged = { isFocused ->
                    onAction(RegisterAction.OnInputTextFocusGain)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            AdventistPortalTextField(
                state = state.emailTextState,
                placeholder = stringResource(Res.string.email_placeholder),
                title = stringResource(Res.string.email),
                supportingText = state.emailError?.asString(),
                isError = state.emailError != null,
                onFocusChanged = { isFocused ->
                    onAction(RegisterAction.OnInputTextFocusGain)
                },
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(16.dp))
            AdventistPortalPasswordTextField(
                state = state.passwordTextState,
                placeholder = stringResource(Res.string.password),
                title = stringResource(Res.string.password),
                supportingText = state.passwordError?.asString()
                    ?: stringResource(Res.string.password_hint),
                isError = state.passwordError != null,
                onFocusChanged = { isFocused ->
                    onAction(RegisterAction.OnInputTextFocusGain)
                },
                onToggleVisibilityClick = {
                    onAction(RegisterAction.OnTogglePasswordVisibilityClick)
                },
                isPasswordVisible = state.isPasswordVisible
            )
            Spacer(modifier = Modifier.height(16.dp))

            AdventistPortalButton(
                text = stringResource(Res.string.register),
                onClick = {
                    onAction(RegisterAction.OnRegisterClick)
                },
                enabled = state.canRegister,
                isLoading = state.isRegistering,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            AdventistPortalButton(
                text = stringResource(Res.string.login),
                onClick = {
                    onAction(RegisterAction.OnLoginClick)
                },
                style = AdventistPortalButtonStyle.SECONDARY,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AdventistPortalTheme {
        RegisterScreen(
            state = RegisterState(),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
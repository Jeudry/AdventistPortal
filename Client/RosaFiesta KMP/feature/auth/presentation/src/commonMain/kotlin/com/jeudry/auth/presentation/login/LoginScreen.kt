package com.adventistportal.auth.presentation.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import adventistportal.feature.auth.presentation.generated.resources.Res
import adventistportal.feature.auth.presentation.generated.resources.create_account
import adventistportal.feature.auth.presentation.generated.resources.email
import adventistportal.feature.auth.presentation.generated.resources.email_placeholder
import adventistportal.feature.auth.presentation.generated.resources.forgot_password
import adventistportal.feature.auth.presentation.generated.resources.login
import adventistportal.feature.auth.presentation.generated.resources.password
import adventistportal.feature.auth.presentation.generated.resources.welcome_back
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
fun LoginRoot(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            LoginEvent.Success -> onLoginSuccess()
        }
    }

    LoginScreen(
        state = state,
        onAction = { action ->
            when(action) {
                LoginAction.OnForgotPasswordClick -> onForgotPasswordClick()
                LoginAction.OnSignUpClick -> onCreateAccountClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    AdventistPortalSnackbarScaffold {
        AdventistPortalAdaptiveFormLayout(
            headerText = stringResource(Res.string.welcome_back),
            errorText = state.error?.asString(),
            logo = {
                AdventistPortalBrandLogo()
            },
            modifier = Modifier
                .fillMaxSize()
        ) {
            AdventistPortalTextField(
                state = state.emailTextFieldState,
                placeholder = stringResource(Res.string.email_placeholder),
                keyboardType = KeyboardType.Email,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(),
                title = stringResource(Res.string.email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            AdventistPortalPasswordTextField(
                state = state.passwordTextFieldState,
                placeholder = stringResource(Res.string.password),
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(LoginAction.OnTogglePasswordVisibility)
                },
                title = stringResource(Res.string.password),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.forgot_password),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        onAction(LoginAction.OnForgotPasswordClick)
                    }
            )
            Spacer(modifier = Modifier.height(24.dp))

            AdventistPortalButton(
                text = stringResource(Res.string.login),
                onClick = {
                    onAction(LoginAction.OnLoginClick)
                },
                enabled = state.canLogin,
                isLoading = state.isLoggingIn,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            AdventistPortalButton(
                text = stringResource(Res.string.create_account),
                onClick = {
                    onAction(LoginAction.OnSignUpClick)
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
private fun LightThemePreview() {
    AdventistPortalTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun DarkThemePreview() {
    AdventistPortalTheme(darkTheme = true) {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}
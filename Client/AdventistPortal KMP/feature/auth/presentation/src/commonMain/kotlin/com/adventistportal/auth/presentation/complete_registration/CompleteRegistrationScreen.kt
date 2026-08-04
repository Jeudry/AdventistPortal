package com.adventistportal.auth.presentation.complete_registration

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import adventistportal.feature.auth.presentation.generated.resources.Res
import adventistportal.feature.auth.presentation.generated.resources.complete_your_profile
import adventistportal.feature.auth.presentation.generated.resources.finish_registration
import adventistportal.feature.auth.presentation.generated.resources.first_name
import adventistportal.feature.auth.presentation.generated.resources.first_name_placeholder
import adventistportal.feature.auth.presentation.generated.resources.last_name
import adventistportal.feature.auth.presentation.generated.resources.last_name_placeholder
import com.adventistportal.core.designsystem.components.brand.AdventistPortalBrandLogo
import com.adventistportal.core.designsystem.components.buttons.AdventistPortalButton
import com.adventistportal.core.designsystem.components.layouts.AdventistPortalAdaptiveFormLayout
import com.adventistportal.core.designsystem.components.layouts.AdventistPortalSnackbarScaffold
import com.adventistportal.core.designsystem.components.textfields.AdventistPortalTextField
import com.adventistportal.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CompleteRegistrationRoot(
    viewModel: CompleteRegistrationViewModel = koinViewModel(),
    onRegistrationComplete: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CompleteRegistrationEvent.Success -> onRegistrationComplete()
        }
    }

    CompleteRegistrationScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun CompleteRegistrationScreen(
    state: CompleteRegistrationState,
    onAction: (CompleteRegistrationAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    AdventistPortalSnackbarScaffold(
        snackbarHostState = snackbarHostState
    ) {
        AdventistPortalAdaptiveFormLayout(
            headerText = stringResource(Res.string.complete_your_profile),
            errorText = state.submitError?.asString(),
            logo = { AdventistPortalBrandLogo() }
        ) {
            AdventistPortalTextField(
                state = state.firstNameTextState,
                placeholder = stringResource(Res.string.first_name_placeholder),
                title = stringResource(Res.string.first_name),
                onFocusChanged = {
                    onAction(CompleteRegistrationAction.OnInputTextFocusGain)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            AdventistPortalTextField(
                state = state.lastNameTextState,
                placeholder = stringResource(Res.string.last_name_placeholder),
                title = stringResource(Res.string.last_name),
                onFocusChanged = {
                    onAction(CompleteRegistrationAction.OnInputTextFocusGain)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            AdventistPortalButton(
                text = stringResource(Res.string.finish_registration),
                onClick = {
                    onAction(CompleteRegistrationAction.OnSubmitClick)
                },
                enabled = state.canSubmit,
                isLoading = state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

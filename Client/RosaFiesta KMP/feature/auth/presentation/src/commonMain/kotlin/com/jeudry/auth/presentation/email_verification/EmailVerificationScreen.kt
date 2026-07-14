package com.adventistportal.auth.presentation.email_verification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import adventistportal.feature.auth.presentation.generated.resources.Res
import adventistportal.feature.auth.presentation.generated.resources.close
import adventistportal.feature.auth.presentation.generated.resources.email_verified_failed
import adventistportal.feature.auth.presentation.generated.resources.email_verified_failed_desc
import adventistportal.feature.auth.presentation.generated.resources.email_verified_successfully
import adventistportal.feature.auth.presentation.generated.resources.email_verified_successfully_desc
import adventistportal.feature.auth.presentation.generated.resources.login
import adventistportal.feature.auth.presentation.generated.resources.verifying_account
import com.adventistportal.core.designsystem.components.brand.AdventistPortalFailureIcon
import com.adventistportal.core.designsystem.components.brand.AdventistPortalSuccessIcon
import com.adventistportal.core.designsystem.components.buttons.AdventistPortalButton
import com.adventistportal.core.designsystem.components.buttons.AdventistPortalButtonStyle
import com.adventistportal.core.designsystem.components.layouts.AdventistPortalAdaptiveResultLayout
import com.adventistportal.core.designsystem.components.layouts.AdventistPortalSimpleResultLayout
import com.adventistportal.core.designsystem.components.layouts.AdventistPortalSnackbarScaffold
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme
import com.adventistportal.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmailVerificationRoot(
    viewModel: EmailVerificationViewModel = koinViewModel(),
    onLoginClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EmailVerificationScreen(
        state = state,
        onAction = { action ->
            when(action) {
                EmailVerificationAction.OnCloseClick -> onCloseClick()
                EmailVerificationAction.OnLoginClick -> onLoginClick()
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun EmailVerificationScreen(
    state: EmailVerificationState,
    onAction: (EmailVerificationAction) -> Unit,
) {
    AdventistPortalSnackbarScaffold {
        AdventistPortalAdaptiveResultLayout {
            when {
                state.isVerifying -> {
                    VerifyingContent(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                state.isVerified -> {
                    AdventistPortalSimpleResultLayout(
                        title = stringResource(Res.string.email_verified_successfully),
                        description = stringResource(Res.string.email_verified_successfully_desc),
                        icon = {
                            AdventistPortalSuccessIcon()
                        },
                        primaryButton = {
                            AdventistPortalButton(
                                text = stringResource(Res.string.login),
                                onClick = {
                                    onAction(EmailVerificationAction.OnLoginClick)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }
                else -> {
                    AdventistPortalSimpleResultLayout(
                        title = stringResource(Res.string.email_verified_failed),
                        description = stringResource(Res.string.email_verified_failed_desc),
                        icon = {
                            Spacer(modifier = Modifier.height(32.dp))
                            AdventistPortalFailureIcon(
                                modifier = Modifier
                                    .size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        },
                        primaryButton = {
                            AdventistPortalButton(
                                text = stringResource(Res.string.close),
                                onClick = {
                                    onAction(EmailVerificationAction.OnCloseClick)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                style = AdventistPortalButtonStyle.SECONDARY
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VerifyingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .heightIn(min = 200.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(
            16.dp,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(64.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(Res.string.verifying_account),
            color = MaterialTheme.colorScheme.extended.textSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
private fun EmailVerificationErrorPreview() {
    AdventistPortalTheme {
        EmailVerificationScreen(
            state = EmailVerificationState(),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun EmailVerificationVerifyingPreview() {
    AdventistPortalTheme {
        EmailVerificationScreen(
            state = EmailVerificationState(
                isVerifying = true
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun EmailVerificationSuccessPreview() {
    AdventistPortalTheme {
        EmailVerificationScreen(
            state = EmailVerificationState(
                isVerified = true
            ),
            onAction = {}
        )
    }
}
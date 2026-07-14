package com.adventistportal.chat.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import adventistportal.feature.chat.presentation.generated.resources.Res
import adventistportal.feature.chat.presentation.generated.resources.cancel
import adventistportal.feature.chat.presentation.generated.resources.contact_adventistportal_support_change_email
import adventistportal.feature.chat.presentation.generated.resources.current_password
import adventistportal.feature.chat.presentation.generated.resources.delete
import adventistportal.feature.chat.presentation.generated.resources.delete_profile_picture
import adventistportal.feature.chat.presentation.generated.resources.delete_profile_picture_desc
import adventistportal.feature.chat.presentation.generated.resources.email
import adventistportal.feature.chat.presentation.generated.resources.new_password
import adventistportal.feature.chat.presentation.generated.resources.password
import adventistportal.feature.chat.presentation.generated.resources.password_change_successful
import adventistportal.feature.chat.presentation.generated.resources.password_hint
import adventistportal.feature.chat.presentation.generated.resources.profile_image
import adventistportal.feature.chat.presentation.generated.resources.save
import adventistportal.feature.chat.presentation.generated.resources.upload_icon
import adventistportal.feature.chat.presentation.generated.resources.upload_image
import com.adventistportal.chat.presentation.profile.components.DragAndDropOverlay
import com.adventistportal.chat.presentation.profile.components.ProfileHeaderSection
import com.adventistportal.chat.presentation.profile.components.ProfileSectionLayout
import com.adventistportal.chat.presentation.profile.mediapicker.rememberDragAndDropTarget
import com.adventistportal.chat.presentation.profile.mediapicker.rememberImagePickerLauncher
import com.adventistportal.core.designsystem.components.avatar.AvatarSize
import com.adventistportal.core.designsystem.components.avatar.AdventistPortalAvatarPhoto
import com.adventistportal.core.designsystem.components.brand.AdventistPortalHorizontalDivider
import com.adventistportal.core.designsystem.components.buttons.AdventistPortalButton
import com.adventistportal.core.designsystem.components.buttons.AdventistPortalButtonStyle
import com.adventistportal.core.designsystem.components.dialogs.AdventistPortalAdaptiveDialogSheetLayout
import com.adventistportal.core.designsystem.components.dialogs.DestructiveConfirmationDialog
import com.adventistportal.core.designsystem.components.textfields.AdventistPortalPasswordTextField
import com.adventistportal.core.designsystem.components.textfields.AdventistPortalTextField
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme
import com.adventistportal.core.designsystem.theme.extended
import com.adventistportal.core.presentation.util.DeviceConfiguration
import com.adventistportal.core.presentation.util.clearFocusOnTap
import com.adventistportal.core.presentation.util.currentDeviceConfiguration
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileRoot(
    onDismiss: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val launcher = rememberImagePickerLauncher { pickedImageData ->
        viewModel.onAction(ProfileAction.OnPictureSelected(
            pickedImageData.bytes,
            pickedImageData.mimeType
        ))
    }

    AdventistPortalAdaptiveDialogSheetLayout(
        onDismiss = onDismiss
    ) {
        ProfileScreen(
            state = state,
            onAction = { action ->
                when(action) {
                    is ProfileAction.OnDismiss -> onDismiss()
                    is ProfileAction.OnUploadPictureClick -> {
                        launcher.launch()
                    }
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )
    }
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
) {
    var isHoveringWithFile by remember {
        mutableStateOf(false)
    }
    val dragAndDropTarget = rememberDragAndDropTarget(
        onHover = { isHovered ->
            isHoveringWithFile = isHovered
        },
        onDrop = { imageData ->
            onAction(ProfileAction.OnPictureSelected(
                bytes = imageData.bytes,
                mimeType = imageData.mimeType
            ))
        }
    )

    Column(
        modifier = Modifier
            .clearFocusOnTap()
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .verticalScroll(rememberScrollState())
            .dragAndDropTarget(
                shouldStartDragAndDrop = { true },
                target = dragAndDropTarget
            )
    ) {
        ProfileHeaderSection(
            username = state.username,
            onCloseClick = {
                onAction(ProfileAction.OnDismiss)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp,
                    horizontal = 20.dp
                )
        )
        AdventistPortalHorizontalDivider()
        ProfileSectionLayout(
            headerText = stringResource(Res.string.profile_image)
        ) {
            Row {
                AdventistPortalAvatarPhoto(
                    displayText = state.userInitials,
                    size = AvatarSize.LARGE,
                    imageUrl = state.profilePictureUrl,
                    onClick = {
                        onAction(ProfileAction.OnUploadPictureClick)
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                FlowRow(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdventistPortalButton(
                        text = stringResource(Res.string.upload_image),
                        onClick = {
                            onAction(ProfileAction.OnUploadPictureClick)
                        },
                        style = AdventistPortalButtonStyle.SECONDARY,
                        enabled = !state.isUploadingImage && !state.isDeletingImage,
                        isLoading = state.isUploadingImage,
                        leadingIcon = {
                            Icon(
                                imageVector = vectorResource(Res.drawable.upload_icon),
                                contentDescription = stringResource(Res.string.upload_image)
                            )
                        }
                    )
                    AdventistPortalButton(
                        text = stringResource(Res.string.delete),
                        onClick = {
                            onAction(ProfileAction.OnDeletePictureClick)
                        },
                        style = AdventistPortalButtonStyle.DESTRUCTIVE_SECONDARY,
                        enabled = !state.isUploadingImage
                                && !state.isDeletingImage
                                && state.profilePictureUrl != null,
                        isLoading = state.isDeletingImage,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(Res.string.delete)
                            )
                        }
                    )
                }
            }

            if(state.imageError != null) {
                Text(
                    text = state.imageError.asString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        AdventistPortalHorizontalDivider()
        ProfileSectionLayout(
            headerText = stringResource(Res.string.email)
        ) {
            AdventistPortalTextField(
                state = state.emailTextState,
                enabled = false,
                supportingText = stringResource(Res.string.contact_adventistportal_support_change_email)
            )
        }
        AdventistPortalHorizontalDivider()
        ProfileSectionLayout(
            headerText = stringResource(Res.string.password)
        ) {
            AdventistPortalPasswordTextField(
                state = state.currentPasswordTextState,
                isPasswordVisible = state.isCurrentPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(ProfileAction.OnToggleCurrentPasswordVisibility)
                },
                placeholder = stringResource(Res.string.current_password),
                isError = state.newPasswordError != null,
            )
            AdventistPortalPasswordTextField(
                state = state.newPasswordTextState,
                isPasswordVisible = state.isNewPasswordVisible,
                onToggleVisibilityClick = {
                    onAction(ProfileAction.OnToggleNewPasswordVisibility)
                },
                placeholder = stringResource(Res.string.new_password),
                isError = state.newPasswordError != null,
                supportingText = state.newPasswordError?.asString()
                    ?: stringResource(Res.string.password_hint)
            )
            if(state.isPasswordChangeSuccessful) {
                Text(
                    text = stringResource(Res.string.password_change_successful),
                    color = MaterialTheme.colorScheme.extended.success,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
            ) {
                AdventistPortalButton(
                    text = stringResource(Res.string.cancel),
                    style = AdventistPortalButtonStyle.SECONDARY,
                    onClick = {
                        onAction(ProfileAction.OnDismiss)
                    }
                )
                AdventistPortalButton(
                    text = stringResource(Res.string.save),
                    onClick = {
                        onAction(ProfileAction.OnChangePasswordClick)
                    },
                    enabled = state.canChangePassword,
                    isLoading = state.isChangingPassword
                )
            }
        }
        val deviceConfiguration = currentDeviceConfiguration()
        if(deviceConfiguration in listOf(
                DeviceConfiguration.MOBILE_PORTRAIT,
                DeviceConfiguration.MOBILE_LANDSCAPE
        )) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    if(isHoveringWithFile) {
        DragAndDropOverlay()
    }

    if(state.showDeleteConfirmationDialog) {
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.delete_profile_picture),
            description = stringResource(Res.string.delete_profile_picture_desc),
            confirmButtonText = stringResource(Res.string.delete),
            cancelButtonText = stringResource(Res.string.cancel),
            onConfirmClick = {
                onAction(ProfileAction.OnConfirmDeleteClick)
            },
            onCancelClick = {
                onAction(ProfileAction.OnDismissDeleteConfirmationDialogClick)
            },
            onDismiss = {
                onAction(ProfileAction.OnDismissDeleteConfirmationDialogClick)
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AdventistPortalTheme {
        ProfileScreen(
            state = ProfileState(),
            onAction = {}
        )
    }
}
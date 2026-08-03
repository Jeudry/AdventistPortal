package com.adventistportal.core.designsystem.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.adventistportal.core.presentation.util.currentDeviceConfiguration

@Composable
fun AdventistPortalAdaptiveDialogSheetLayout(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val configuration = currentDeviceConfiguration()
    if(configuration.isMobile) {
        AdventistPortalBottomSheet(
            onDismiss = onDismiss,
            content = content
        )
    } else {
        AdventistPortalDialogContent(
            onDismiss = onDismiss,
            content = content
        )
    }
}
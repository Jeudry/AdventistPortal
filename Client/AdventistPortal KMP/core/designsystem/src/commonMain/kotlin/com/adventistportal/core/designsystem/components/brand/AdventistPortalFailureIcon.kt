package com.adventistportal.core.designsystem.components.brand

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import adventistportal.core.designsystem.generated.resources.Res
import adventistportal.core.designsystem.generated.resources.success_checkmark
import com.adventistportal.core.designsystem.theme.extended
import org.jetbrains.compose.resources.vectorResource

/// <summary>
/// Displays a failure icon
/// </summary>
@Composable
fun AdventistPortalFailureIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = Icons.Default.Close,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error,
        modifier = modifier
    )
}
package com.adventistportal.core.designsystem.components.brand

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import adventistportal.core.designsystem.generated.resources.Res
import adventistportal.core.designsystem.generated.resources.success_checkmark
import com.adventistportal.core.designsystem.theme.extended
import org.jetbrains.compose.resources.vectorResource

/// <summary>
/// Displays a success icon
/// </summary>
@Composable
fun AdventistPortalSuccessIcon(
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = vectorResource(Res.drawable.success_checkmark),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.extended.success,
        modifier = modifier
    )
}
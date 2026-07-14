package com.adventistportal.core.designsystem.components.buttons

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/// <summary>
/// Floating action button with AdventistPortal branding
/// </summary>
@Composable
fun AdventistPortalFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        content = content
    )
}

@Composable
@Preview
fun AdventistPortalFloatingActionButtonFloatingActionButtonPreview() {
    AdventistPortalTheme {
        AdventistPortalFloatingActionButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }
}
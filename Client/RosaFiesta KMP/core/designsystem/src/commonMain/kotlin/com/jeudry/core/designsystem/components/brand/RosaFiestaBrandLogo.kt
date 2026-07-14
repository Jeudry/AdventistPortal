package com.adventistportal.core.designsystem.components.brand

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.vectorResource
import adventistportal.core.designsystem.generated.resources.Res
import adventistportal.core.designsystem.generated.resources.logo_adventistportal

/// <summary>
/// Displays the AdventistPortal brand logo as an icon
/// </summary>
@Composable
fun AdventistPortalBrandLogo(
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = vectorResource(Res.drawable.logo_adventistportal),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}
package com.adventistportal.core.designsystem.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.adventistportal.core.designsystem.components.layouts.AdventistPortalAdaptiveResultLayout
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme

@Composable
@PreviewLightDark
@PreviewScreenSizes
fun AdventistPortalAdaptiveResultLayoutPreview() {
    AdventistPortalTheme {
        AdventistPortalAdaptiveResultLayout(
            modifier = Modifier
                .fillMaxSize(),
            content = {
                Text(
                    text = "Registration successful!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}
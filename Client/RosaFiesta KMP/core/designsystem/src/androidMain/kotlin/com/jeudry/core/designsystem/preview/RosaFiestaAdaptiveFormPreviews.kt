package com.adventistportal.core.designsystem.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.adventistportal.core.designsystem.components.brand.AdventistPortalBrandLogo
import com.adventistportal.core.designsystem.components.layouts.AdventistPortalAdaptiveFormLayout
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme

@Composable
@PreviewLightDark
@PreviewScreenSizes
fun AdventistPortalAdaptiveFormLayoutLightPreview() {
    AdventistPortalTheme {
        AdventistPortalAdaptiveFormLayout(
            headerText = "Welcome to AdventistPortal!",
            errorText = "Login failed!",
            logo = { AdventistPortalBrandLogo() },
            formContent = {
                Text(
                    text = "Sample form title",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Sample form title 2",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}
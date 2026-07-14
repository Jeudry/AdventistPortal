package com.adventistportal.core.designsystem.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme
import com.adventistportal.core.designsystem.theme.extended
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class AdventistPortalButtonStyle {
    PRIMARY,
    DESTRUCTIVE_PRIMARY,
    SECONDARY,
    DESTRUCTIVE_SECONDARY,
    TEXT
}

@Composable
fun AdventistPortalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AdventistPortalButtonStyle = AdventistPortalButtonStyle.PRIMARY,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    val colors = when(style) {
        AdventistPortalButtonStyle.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.extended.disabledFill,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        AdventistPortalButtonStyle.DESTRUCTIVE_PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            disabledContainerColor = MaterialTheme.colorScheme.extended.disabledFill,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        AdventistPortalButtonStyle.SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.extended.textSecondary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        AdventistPortalButtonStyle.DESTRUCTIVE_SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.error,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
        AdventistPortalButtonStyle.TEXT -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.tertiary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.extended.textDisabled
        )
    }

    val defaultBorderStroke = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.extended.disabledOutline
    )
    val border = when {
        style == AdventistPortalButtonStyle.PRIMARY && !enabled -> defaultBorderStroke
        style == AdventistPortalButtonStyle.SECONDARY -> defaultBorderStroke
        style == AdventistPortalButtonStyle.DESTRUCTIVE_PRIMARY && !enabled -> defaultBorderStroke
        style == AdventistPortalButtonStyle.DESTRUCTIVE_SECONDARY -> {
            val borderColor = if(enabled) {
                MaterialTheme.colorScheme.extended.destructiveSecondaryOutline
            } else {
                MaterialTheme.colorScheme.extended.disabledOutline
            }
            BorderStroke(
                width = 1.dp,
                color = borderColor
            )
        }
        else -> null
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = colors,
        border = border
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(6.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(15.dp)
                    .alpha(
                        alpha = if(isLoading) 1f else 0f
                    ),
                strokeWidth = 1.5.dp,
                color = Color.Black
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(
                    if(isLoading) 0f else 1f
                )
            ) {
                leadingIcon?.invoke()
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
@Preview
fun AdventistPortalButtonPrimaryButtonPreview() {
    AdventistPortalTheme(
        darkTheme = true
    ) {
        AdventistPortalButton(
            text = "Hello world!",
            onClick = {},
            style = AdventistPortalButtonStyle.PRIMARY
        )
    }
}

@Composable
@Preview
fun AdventistPortalButtonSecondaryButtonPreview() {
    AdventistPortalTheme(
        darkTheme = true
    ) {
        AdventistPortalButton(
            text = "Hello world!",
            onClick = {},
            style = AdventistPortalButtonStyle.SECONDARY
        )
    }
}

@Composable
@Preview
fun AdventistPortalButtonDestructivePrimaryButtonPreview() {
    AdventistPortalTheme(
        darkTheme = true
    ) {
        AdventistPortalButton(
            text = "Hello world!",
            onClick = {},
            style = AdventistPortalButtonStyle.DESTRUCTIVE_PRIMARY
        )
    }
}

@Composable
@Preview
fun AdventistPortalButtonDestructiveSecondaryButtonPreview() {
    AdventistPortalTheme(
        darkTheme = true
    ) {
        AdventistPortalButton(
            text = "Hello world!",
            onClick = {},
            style = AdventistPortalButtonStyle.DESTRUCTIVE_SECONDARY
        )
    }
}

@Composable
@Preview
fun AdventistPortalButtonTextButtonPreview() {
    AdventistPortalTheme(
        darkTheme = true
    ) {
        AdventistPortalButton(
            text = "Hello world!",
            onClick = {},
            style = AdventistPortalButtonStyle.TEXT
        )
    }
}
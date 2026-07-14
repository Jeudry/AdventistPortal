package com.adventistportal.core.designsystem.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/// <summary>
/// Displays a row of overlapping avatar images
/// </summary>
@Composable
fun AdventistPortalStackedAvatars(
    avatars: List<ChatParticipantUi>,
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.SMALL,
    maxVisible: Int = 2,
    overlapPercentage: Float = 0.4f
) {
    val overlapOffset = -(size.dp * overlapPercentage)

    val visibleAvatars = avatars.take(maxVisible)
    val remainingCount = (avatars.size - maxVisible).coerceAtLeast(0)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(overlapOffset),
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleAvatars.forEach { avatarUi ->
            AdventistPortalAvatarPhoto(
                displayText = avatarUi.initials,
                size = size,
                imageUrl = avatarUi.imageUrl
            )
        }

        if(remainingCount > 0) {
            AdventistPortalAvatarPhoto(
                displayText = "$remainingCount+",
                size = size,
                textColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
@Preview
fun AdventistPortalStackedAvatarsPreview() {
    AdventistPortalTheme {
        AdventistPortalStackedAvatars(
            avatars = listOf(
                ChatParticipantUi(
                    id = "1",
                    username = "Philipp",
                    initials = "PH",
                ),
                ChatParticipantUi(
                    id = "2",
                    username = "John",
                    initials = "JO",
                ),
                ChatParticipantUi(
                    id = "3",
                    username = "Sabrina",
                    initials = "SA",
                ),
                ChatParticipantUi(
                    id = "4",
                    username = "Cinderella",
                    initials = "CI",
                ),
            )
        )
    }
}
package com.adventistportal.chat.presentation.chat_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import adventistportal.core.designsystem.generated.resources.log_out_icon
import adventistportal.core.designsystem.generated.resources.logo_adventistportal
import adventistportal.feature.chat.presentation.generated.resources.Res
import adventistportal.feature.chat.presentation.generated.resources.logout
import adventistportal.feature.chat.presentation.generated.resources.profile_settings
import adventistportal.feature.chat.presentation.generated.resources.users_icon
import com.adventistportal.chat.presentation.components.ChatHeader
import com.adventistportal.core.designsystem.components.avatar.ChatParticipantUi
import com.adventistportal.core.designsystem.components.avatar.AdventistPortalAvatarPhoto
import com.adventistportal.core.designsystem.components.dropdown.AdventistPortalDropDownMenu
import com.adventistportal.core.designsystem.components.dropdown.DropDownItem
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme
import com.adventistportal.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import adventistportal.core.designsystem.generated.resources.Res as DesignSystemRes

@Composable
fun ChatListHeader(
    localParticipant: ChatParticipantUi?,
    isUserMenuOpen: Boolean,
    onUserAvatarClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChatHeader(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = vectorResource(DesignSystemRes.drawable.logo_adventistportal),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Chirp",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.extended.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            ProfileAvatarSection(
                localParticipant = localParticipant,
                isMenuOpen = isUserMenuOpen,
                onClick = onUserAvatarClick,
                onDismissMenu = onDismissMenu,
                onProfileSettingsClick = onProfileSettingsClick,
                onLogoutClick = onLogoutClick,
            )
        }
    }
}

@Composable
fun ProfileAvatarSection(
    localParticipant: ChatParticipantUi?,
    isMenuOpen: Boolean,
    onClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onProfileSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        if(localParticipant != null) {
            AdventistPortalAvatarPhoto(
                displayText = localParticipant.initials,
                imageUrl = localParticipant.imageUrl,
                onClick = onClick
            )
        }

        AdventistPortalDropDownMenu(
            isOpen = isMenuOpen,
            onDismiss = onDismissMenu,
            items = listOf(
                DropDownItem(
                    title = stringResource(Res.string.profile_settings),
                    icon = vectorResource(Res.drawable.users_icon),
                    contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                    onClick = onProfileSettingsClick
                ),
                DropDownItem(
                    title = stringResource(Res.string.logout),
                    icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                    contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                    onClick = onLogoutClick
                ),
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ChatListHeaderPreview() {
    AdventistPortalTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ChatListHeader(
                localParticipant = ChatParticipantUi(
                    id = "1",
                    username = "Philipp",
                    initials = "PH",
                ),
                isUserMenuOpen = true,
                onUserAvatarClick = {},
                onDismissMenu = {},
                onProfileSettingsClick = {},
                onLogoutClick = {}
            )
        }
    }
}
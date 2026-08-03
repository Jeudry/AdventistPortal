package com.adventistportal.chat.presentation.chat_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import adventistportal.core.designsystem.generated.resources.arrow_left_icon
import adventistportal.core.designsystem.generated.resources.dots_icon
import adventistportal.core.designsystem.generated.resources.log_out_icon
import adventistportal.feature.chat.presentation.generated.resources.Res
import adventistportal.feature.chat.presentation.generated.resources.chat_members
import adventistportal.feature.chat.presentation.generated.resources.go_back
import adventistportal.feature.chat.presentation.generated.resources.leave_chat
import adventistportal.feature.chat.presentation.generated.resources.open_chat_options_menu
import adventistportal.feature.chat.presentation.generated.resources.users_icon
import com.adventistportal.chat.domain.models.ChatMessage
import com.adventistportal.chat.domain.models.ChatMessageDeliveryStatus
import com.adventistportal.chat.presentation.components.ChatHeader
import com.adventistportal.chat.presentation.components.ChatItemHeaderRow
import com.adventistportal.chat.presentation.model.ChatUi
import com.adventistportal.core.designsystem.components.avatar.ChatParticipantUi
import com.adventistportal.core.designsystem.components.buttons.AdventistPortalIconButton
import com.adventistportal.core.designsystem.components.dropdown.AdventistPortalDropDownMenu
import com.adventistportal.core.designsystem.components.dropdown.DropDownItem
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme
import com.adventistportal.core.designsystem.theme.extended
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import adventistportal.core.designsystem.generated.resources.Res as DesignSystemRes

@Composable
fun ChatDetailHeader(
    chatUi: ChatUi?,
    isDetailPresent: Boolean,
    isChatOptionsDropDownOpen: Boolean,
    onChatOptionsClick: () -> Unit,
    onDismissChatOptions: ()  -> Unit,
    onManageChatClick: () -> Unit,
    onLeaveChatClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if(!isDetailPresent) {
            AdventistPortalIconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = vectorResource(DesignSystemRes.drawable.arrow_left_icon),
                    contentDescription = stringResource(Res.string.go_back),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }
        }

        if(chatUi != null) {
            val isGroupChat = chatUi.otherParticipants.size > 1
            ChatItemHeaderRow(
                chat = chatUi,
                isGroupChat = isGroupChat,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onManageChatClick()
                    }
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Box {
            AdventistPortalIconButton(
                onClick = onChatOptionsClick
            ) {
                Icon(
                    imageVector = vectorResource(DesignSystemRes.drawable.dots_icon),
                    contentDescription = stringResource(Res.string.open_chat_options_menu),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.extended.textSecondary
                )
            }

            AdventistPortalDropDownMenu(
                isOpen = isChatOptionsDropDownOpen,
                onDismiss = onDismissChatOptions,
                items = listOf(
                    DropDownItem(
                        title = stringResource(Res.string.chat_members),
                        icon = vectorResource(Res.drawable.users_icon),
                        contentColor = MaterialTheme.colorScheme.extended.textSecondary,
                        onClick = onManageChatClick
                    ),
                    DropDownItem(
                        title = stringResource(Res.string.leave_chat),
                        icon = vectorResource(DesignSystemRes.drawable.log_out_icon),
                        contentColor = MaterialTheme.colorScheme.extended.destructiveHover,
                        onClick = onLeaveChatClick
                    ),
                )
            )
        }
    }
}

@Composable
@Preview
fun ChatDetailHeaderPreview() {
    AdventistPortalTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ChatHeader {
                ChatDetailHeader(
                    isDetailPresent = false,
                    isChatOptionsDropDownOpen = true,
                    chatUi = ChatUi(
                        id = "1",
                        localParticipant = ChatParticipantUi(
                            id = "1",
                            username = "Philipp",
                            initials = "PH",
                        ),
                        otherParticipants = listOf(
                            ChatParticipantUi(
                                id = "2",
                                username = "Cinderella",
                                initials = "CI",
                            ),
                            ChatParticipantUi(
                                id = "3",
                                username = "Josh",
                                initials = "JO",
                            )
                        ),
                        lastMessage = ChatMessage(
                            id = "1",
                            chatId = "1",
                            content = "This is a last chat message that was sent by Philipp " +
                                    "and goes over multiple lines to showcase the ellipsis",
                            createdAt = Clock.System.now(),
                            senderId = "1",
                            deliveryStatus = ChatMessageDeliveryStatus.SENT
                        ),
                        lastMessageSenderUsername = "Philipp"
                    ),
                    onChatOptionsClick = {},
                    onManageChatClick = {},
                    onLeaveChatClick = {},
                    onDismissChatOptions = {},
                    onBackClick = {},
                )
            }
        }
    }
}
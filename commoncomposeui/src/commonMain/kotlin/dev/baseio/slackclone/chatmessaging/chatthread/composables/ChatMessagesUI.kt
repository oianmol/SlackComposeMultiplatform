package dev.baseio.slackclone.chatmessaging.chatthread.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.chatmessaging.chatthread.ChatScreenComponent
import dev.baseio.slackclone.chatmessaging.chatthread.ChatViewModel
import dev.baseio.slackclone.common.extensions.calendar
import dev.baseio.slackclone.common.extensions.formattedMonthDate
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackdomain.model.message.DomainLayerMessages

@Composable
internal fun ChatMessagesUI(
    screenComponent: ChatScreenComponent,
    viewModel: ChatViewModel = screenComponent.chatViewModel,
    modifier: Modifier,
    alertLongClick: (DomainLayerMessages.SKMessage) -> Unit
) {
    val messages by viewModel.chatMessagesFlow.collectAsState()
    val members by viewModel.channelMembers.collectAsState()
    val listState = rememberLazyListState()
    val threshold = 3

    LazyColumn(state = listState, reverseLayout = true, modifier = modifier) {
        var lastDrawnMessage: String?
        for (messageIndex in messages.indices) {
            val message = messages[messageIndex]
            item {
                ChatMessage(
                    message,
                    alertLongClick,
                    members.firstOrNull { it.uuid == message.sender },
                    onClickHash = {}
                )
                if (messageIndex + threshold >= messages.lastIndex) {
                    SideEffect {
                        viewModel.skMessagePagination.loadNextPage()
                    }
                }
            }
            lastDrawnMessage = message.createdDate.calendar().formattedMonthDate()
            if (!isLastMessage(messageIndex, messages)) {
                val nextMessageMonth = messages[messageIndex + 1].createdDate.calendar().formattedMonthDate()
                if (nextMessageMonth != lastDrawnMessage) {
                    item {
                        ChatHeader(message.createdDate)
                    }
                }
            } else {
                item {
                    ChatHeader(message.createdDate)
                }
            }
        }
    }
}

private fun isLastMessage(
    messageIndex: Int,
    messages: List<DomainLayerMessages.SKMessage>
) = messageIndex == messages.size.minus(1)

@Composable
internal fun ChatHeader(createdDate: Long) {
    Column(Modifier.padding(start = 8.dp, end = 8.dp)) {
        Text(
            createdDate.calendar().formattedMonthDate(),
            style = SlackCloneTypography.subtitle2.copy(
                fontWeight = FontWeight.Bold, color = LocalSlackCloneColor.current.textPrimary
            ),
            modifier = Modifier.padding(4.dp)
        )
        Divider(color = LocalSlackCloneColor.current.lineColor, thickness = 0.5.dp)
    }
}

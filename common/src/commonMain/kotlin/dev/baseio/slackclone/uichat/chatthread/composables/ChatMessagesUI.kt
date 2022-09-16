package dev.baseio.slackclone.uichat.chatthread.composables

import mainDispatcher
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.common.extensions.calendar
import dev.baseio.slackclone.common.extensions.formattedMonthDate
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.uichat.chatthread.ChatScreenVM
import dev.baseio.slackdomain.model.message.DomainLayerMessages

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessagesUI(viewModel: ChatScreenVM, modifier: Modifier) {
  val flowState by viewModel.chatMessagesFlow.collectAsState(mainDispatcher)
  val messages by flowState.collectAsState(emptyList(), mainDispatcher)
  val listState = rememberLazyListState()

  LazyColumn(state = listState, reverseLayout = true, modifier = modifier) {
    var lastDrawnMessage: String? = null
    for (messageIndex in messages.indices) {
      val message = messages[messageIndex]
      item {
        ChatMessage(message)
      }
      lastDrawnMessage = message.createdDate.calendar().formattedMonthDate()
      if (!isLastMessage(messageIndex, messages)) {
        val nextMessageMonth =
          messages[messageIndex + 1].createdDate.calendar().formattedMonthDate()
        if (nextMessageMonth != lastDrawnMessage) {
          stickyHeader {
            ChatHeader(message.createdDate)
          }
        }
      } else {
        stickyHeader {
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
private fun ChatHeader(createdDate: Long) {
  Column(Modifier.padding(start = 8.dp, end = 8.dp)) {
    Text(
      createdDate.calendar().formattedMonthDate(),
      style = SlackCloneTypography.subtitle2.copy(
        fontWeight = FontWeight.Bold,
        color = SlackCloneColorProvider.colors.textPrimary
      ), modifier = Modifier.padding(4.dp)
    )
    Divider(color = SlackCloneColorProvider.colors.lineColor, thickness = 0.5.dp)
  }
}

package dev.baseio.slackclone.uichannels.directmessages

import mainDispatcher
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.chatcore.views.DMLastMessageItem

@Composable
fun DMChannelsList(
  onItemClick: (UiLayerChannels.SKChannel) -> Unit,
  channelVM: MessageViewModel
) {
  val channels by channelVM.channels.collectAsState(mainDispatcher)
  val channelsFlow by channels.collectAsState(emptyList(),mainDispatcher)
  val listState = rememberLazyListState()

  LazyColumn(state = listState) {
    for (channelIndex in 0 until channelsFlow.size) {
      val channel = channelsFlow.get(channelIndex)!!

      item {
        DMLastMessageItem({
          onItemClick(it)
        }, channelVM.mapToUI(channel.channel), channel.message)
      }
    }
  }
}
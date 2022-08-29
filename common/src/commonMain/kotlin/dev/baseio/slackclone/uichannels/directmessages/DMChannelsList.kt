package dev.baseio.slackclone.uichannels.directmessages

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.chatcore.views.DMLastMessageItem
import org.koin.java.KoinJavaComponent.inject

@Composable
fun DMChannelsList(
  onItemClick: (UiLayerChannels.SlackChannel) -> Unit,
) {
  val channelVM: MessageViewModel by inject(MessageViewModel::class.java)
  val channels by channelVM.channels.collectAsState()
  val channelsFlow by channels.collectAsState(emptyList())
  val listState = rememberLazyListState()

  LaunchedEffect(key1 = Unit) {
    channelVM.refresh()
  }

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
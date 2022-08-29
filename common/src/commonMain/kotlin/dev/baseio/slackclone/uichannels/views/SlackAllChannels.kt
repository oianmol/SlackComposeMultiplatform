package dev.baseio.slackclone.uichannels.views

import dev.baseio.slackclone.chatcore.data.ExpandCollapseModel
import dev.baseio.slackclone.uichannels.SlackChannelVM
import androidx.compose.runtime.*
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import org.koin.java.KoinJavaComponent.inject

@Composable
fun SlackAllChannels(
  onItemClick: (UiLayerChannels.SlackChannel) -> Unit = {},
  onClickAdd: () -> Unit
) {
  val channelVM: SlackChannelVM by inject(SlackChannelVM::class.java)

  val recent = "Channels"
  val channelsFlow = channelVM.channels.collectAsState()
  val channels by channelsFlow.value.collectAsState(emptyList())

  LaunchedEffect(key1 = Unit) {
    channelVM.allChannels()
  }

  var expandCollapseModel by remember {
    mutableStateOf(
      ExpandCollapseModel(
        1, recent,
        needsPlusButton = true,
        isOpen = false
      )
    )
  }
  SKExpandCollapseColumn(expandCollapseModel = expandCollapseModel, onItemClick = onItemClick, {
    expandCollapseModel = expandCollapseModel.copy(isOpen = it)
  }, channels, onClickAdd)
}
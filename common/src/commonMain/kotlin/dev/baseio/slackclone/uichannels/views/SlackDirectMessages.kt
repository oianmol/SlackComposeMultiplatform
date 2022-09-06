package dev.baseio.slackclone.uichannels.views

import dev.baseio.slackclone.chatcore.data.ExpandCollapseModel
import dev.baseio.slackclone.uichannels.SlackChannelVM
import androidx.compose.runtime.*

import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.slackComponent

@Composable
fun SlackDirectMessages(
  onItemClick: (UiLayerChannels.SlackChannel) -> Unit = {},
  onClickAdd: () -> Unit
) {
  val channelVM: SlackChannelVM  = slackComponent.provideSlackChannelVM()

  val recent = "DMs"
  val channelsFlow = channelVM.channels.collectAsState()
  val channels by channelsFlow.value.collectAsState(emptyList())

  LaunchedEffect(key1 = Unit) {
    channelVM.loadDirectMessageChannels()
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
  SKExpandCollapseColumn(expandCollapseModel, onItemClick, {
    expandCollapseModel = expandCollapseModel.copy(isOpen = it)
  }, channels, onClickAdd)
}

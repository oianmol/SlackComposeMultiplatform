package dev.baseio.slackclone.uichannels.views

import mainDispatcher
import dev.baseio.slackclone.chatcore.data.ExpandCollapseModel
import dev.baseio.slackclone.uichannels.SlackChannelVM
import androidx.compose.runtime.*

import dev.baseio.slackclone.chatcore.data.UiLayerChannels


@Composable
fun SlackDirectMessages(
  onItemClick: (UiLayerChannels.SKChannel) -> Unit = {},
  onClickAdd: () -> Unit,
  channelVM: SlackChannelVM
) {
  val recent = "DMs"
  val channelsFlow = channelVM.channels.collectAsState(mainDispatcher)
  val channels by channelsFlow.value.collectAsState(emptyList(),mainDispatcher)

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

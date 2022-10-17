package dev.baseio.slackclone.uichannels.views

import mainDispatcher
import dev.baseio.slackclone.chatcore.data.ExpandCollapseModel
import dev.baseio.slackclone.uichannels.SlackChannelVM
import androidx.compose.runtime.*
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

@Composable
fun SlackAllChannels(
  onItemClick: (DomainLayerChannels.SKChannel) -> Unit = {},
  onClickAdd: () -> Unit,
  channelVM: SlackChannelVM,
) {
  val recent = "Channels"
  val channelsFlow = channelVM.channels.collectAsState(mainDispatcher)
  val channels by channelsFlow.value.collectAsState(emptyList(), mainDispatcher)

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
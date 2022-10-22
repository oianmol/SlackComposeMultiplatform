package dev.baseio.slackclone.uichannels.views

import mainDispatcher
import dev.baseio.slackclone.uichannels.SlackChannelComponent
import dev.baseio.slackclone.chatcore.data.ExpandCollapseModel
import androidx.compose.runtime.*
import dev.baseio.slackdomain.model.channel.DomainLayerChannels


@Composable
fun SlackRecentChannels(
  onItemClick: (DomainLayerChannels.SKChannel) -> Unit = {},
  onClickAdd: () -> Unit,
  channelVM: SlackChannelComponent
) {
  val recent = "Recent"
  val channelsFlow = channelVM.channels.collectAsState(mainDispatcher)
  val channels by channelsFlow.value.collectAsState(emptyList(), mainDispatcher)

  LaunchedEffect(key1 = Unit) {
    channelVM.loadRecentChannels()
  }

  var expandCollapseModel by remember {
    mutableStateOf(
      ExpandCollapseModel(
        1, recent,
        needsPlusButton = false,
        isOpen = true
      )
    )
  }
  SKExpandCollapseColumn(expandCollapseModel, onItemClick, {
    expandCollapseModel = expandCollapseModel.copy(isOpen = it)
  }, channels, onClickAdd)
}
package dev.baseio.slackclone.uichannels.views

import mainDispatcher
import dev.baseio.slackclone.uichannels.SlackChannelVM
import dev.baseio.slackclone.chatcore.data.ExpandCollapseModel
import androidx.compose.runtime.*
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.data.injection.RecentChatsQualifier
import dev.baseio.slackclone.slackComponent

@Composable
fun SlackRecentChannels(
  onItemClick: (UiLayerChannels.SKChannel) -> Unit = {},
  onClickAdd: () -> Unit
) {
  val channelVM: SlackChannelVM = slackComponent.provideSlackChannelVM(RecentChatsQualifier)

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
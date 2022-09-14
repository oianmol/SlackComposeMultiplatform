package dev.baseio.slackclone.uichannels.views

import mainDispatcher
import dev.baseio.slackclone.uichannels.SlackChannelVM
import dev.baseio.slackclone.chatcore.data.ExpandCollapseModel
import androidx.compose.runtime.*

import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.data.injection.RecentChatsQualifier
import dev.baseio.slackclone.data.injection.StarredChatsQualifier
import dev.baseio.slackclone.slackComponent

@Composable
fun SlackStarredChannels(
  onItemClick: (UiLayerChannels.SKChannel) -> Unit = {},
  onClickAdd: () -> Unit
) {
  val channelVM: SlackChannelVM = slackComponent.provideSlackChannelVM(StarredChatsQualifier)

  val recent = "Starred"
  val channelsFlow = channelVM.channels.collectAsState(mainDispatcher)
  val channels by channelsFlow.value.collectAsState(emptyList(),mainDispatcher)

  LaunchedEffect(key1 = Unit) {
    channelVM.loadStarredChannels()
  }
  var expandCollapseModel by remember {
    mutableStateOf(
      ExpandCollapseModel(
        1, recent,
        needsPlusButton = false,
        isOpen = false
      )
    )
  }
  SKExpandCollapseColumn(expandCollapseModel, onItemClick, {
    expandCollapseModel = expandCollapseModel.copy(isOpen = it)
  }, channels, onClickAdd)
}
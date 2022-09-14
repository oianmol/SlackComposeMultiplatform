package dev.baseio.slackclone.uidashboard.compose

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import kotlinx.coroutines.flow.*

class DashboardVM : ViewModel() {
  val selectedChatChannel = MutableStateFlow<UiLayerChannels.SKChannel?>(null)
  val isChatViewClosed = MutableStateFlow(true)

}
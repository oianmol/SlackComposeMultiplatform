package dev.baseio.slackclone.uidashboard.compose

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchUsers
import kotlinx.coroutines.flow.*

class DashboardVM(
  private val useCaseFetchUsers: UseCaseFetchUsers,
) : ViewModel() {

  val selectedChatChannel = MutableStateFlow<UiLayerChannels.SKChannel?>(null)
  val isChatViewClosed = MutableStateFlow(true)

}
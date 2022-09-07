package dev.baseio.slackclone.uidashboard.compose

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.domain.usecases.channels.UseCaseCreateChannels
import dev.baseio.slackclone.domain.usecases.channels.UseCaseFetchUsers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardVM(
  private val useCaseFetchUsers: UseCaseFetchUsers,
  private val useCaseSaveChannel: UseCaseCreateChannels
) : ViewModel() {

  val selectedChatChannel = MutableStateFlow<UiLayerChannels.SlackChannel?>(null)
  val isChatViewClosed = MutableStateFlow(true)

  init {
    preloadUsers()
  }

  private fun preloadUsers() {
    viewModelScope.launch {
      val users = useCaseFetchUsers.perform(10)
      useCaseSaveChannel.perform(users)
    }
  }

}
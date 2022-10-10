package dev.baseio.slackclone.uidashboard.vm

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*

class DashboardVM(private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace) : ViewModel() {
  val selectedChatChannel = MutableStateFlow<UiLayerChannels.SKChannel?>(null)
  var selectedWorkspace = MutableStateFlow<DomainLayerWorkspaces.SKWorkspace?>(null)
  val isChatViewClosed = MutableStateFlow(true)

  init {
    useCaseGetSelectedWorkspace.performStreaming(Unit).onEach {
      if (selectedWorkspace.value != it) {
        selectedChatChannel.value = null
        isChatViewClosed.value = true
      }
      selectedWorkspace.value = it
    }.launchIn(viewModelScope)
  }

}
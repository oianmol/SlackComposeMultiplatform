package dev.baseio.slackclone.uidashboard.vm

import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*
import ViewModel
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import kotlinx.coroutines.launch

class DashboardVM(
    useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
    useCaseFetchAndSaveWorkspaces: UseCaseFetchAndSaveWorkspaces
) : ViewModel() {
    val selectedChatChannel = MutableStateFlow<DomainLayerChannels.SKChannel?>(null)
    var selectedWorkspace = MutableStateFlow<DomainLayerWorkspaces.SKWorkspace?>(null)
    val isChatViewClosed = MutableStateFlow(true)

    init {
        viewModelScope.launch {
            useCaseFetchAndSaveWorkspaces.invoke()
        }
        useCaseGetSelectedWorkspace.invokeFlow().onEach {
            if (selectedWorkspace.value != it) {
                selectedChatChannel.value = null
                isChatViewClosed.value = true
            }
            selectedWorkspace.value = it
        }.launchIn(viewModelScope)
    }

}
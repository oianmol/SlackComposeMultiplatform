package dev.baseio.slackclone.uidashboard.home

import dev.baseio.slackdomain.usecases.chat.UseCaseFetchAndUpdateChangeInMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannels
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*
import ViewModel
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.launch

class HomeScreenVM(
    private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
    private val useCaseFetchChannels: UseCaseFetchAndSaveChannels,
    private val useCaseFetchAndSaveUsers: UseCaseFetchAndSaveUsers,
) : ViewModel() {
    var lastSelectedWorkspace = MutableStateFlow(flow())
        private set

    fun flow() = useCaseGetSelectedWorkspace.invokeFlow()

    init {
        lastSelectedWorkspace.value.onEach { workspace ->
            workspace?.uuid?.let { workspaceId ->
                viewModelScope.launch {
                    useCaseFetchChannels.invoke(workspaceId, 0, 20)
                    useCaseFetchAndSaveUsers(workspaceId)
                }
            }
        }.launchIn(viewModelScope)
    }
}
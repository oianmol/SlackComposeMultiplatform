package dev.baseio.slackclone.uidashboard.home

import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannels
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*
import ViewModel
class HomeScreenVM(
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
  private val useCaseFetchChannels: UseCaseFetchChannels,
  private val useCaseFetchAndSaveUsers: UseCaseFetchAndSaveUsers
) : ViewModel() {
  var lastSelectedWorkspace = MutableStateFlow(flow())
    private set

  fun flow() = useCaseGetSelectedWorkspace.performStreaming(Unit)

  init {
    lastSelectedWorkspace.value.onEach { workspace ->
      workspace?.uuid?.let { workspaceId ->
        useCaseFetchChannels.performStreaming(workspaceId).onEach {
          // we don't do anything here!
          println(it)
        }.launchIn(viewModelScope)
        useCaseFetchAndSaveUsers.performStreaming(workspaceId).onEach {
          //we will fetch all the users locally here!
          println(it)
        }.launchIn(viewModelScope)
      }
    }.launchIn(viewModelScope)
  }
}
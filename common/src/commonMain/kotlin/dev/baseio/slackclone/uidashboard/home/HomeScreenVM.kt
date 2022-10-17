package dev.baseio.slackclone.uidashboard.home

import dev.baseio.slackdomain.usecases.chat.UseCaseFetchAndUpdateChangeInMessages
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndUpdateChangeInUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndUpdateChangeInChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannels
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*
import ViewModel
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeScreenVM(
  private val useCaseObserveMessages: UseCaseFetchAndUpdateChangeInMessages,
  private val useCaseObserveUsers: UseCaseFetchAndUpdateChangeInUsers,
  private val useCaseObserveChannels: UseCaseFetchAndUpdateChangeInChannels,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
  private val useCaseFetchChannels: UseCaseFetchAndSaveChannels,
  private val useCaseFetchAndSaveUsers: UseCaseFetchAndSaveUsers,
  private val skKeyValueData: SKKeyValueData
) : ViewModel() {
  private var observeNewMessagesJob: Job? = null
  private var useCaseObserveUsersJob: Job? = null
  private var useCaseObserveChannelsJob: Job? = null
  var lastSelectedWorkspace = MutableStateFlow(flow())
    private set

  var lastWorkspace: String? = null
  fun flow() = useCaseGetSelectedWorkspace.invokeFlow()

  init {
    lastSelectedWorkspace.value.onEach { workspace ->
      workspace?.uuid?.let { workspaceId ->
        cancelJobIfWorkspaceChanged(workspaceId)
        lastWorkspace = workspaceId
        val user =
          skKeyValueData.skUser()// TODO is this the best way to fetch user ?
        observeForUserData(workspaceId, user)
        viewModelScope.launch {
          useCaseFetchChannels.invoke(workspaceId, 0, 20)
          useCaseFetchAndSaveUsers(workspaceId)
        }
      }
    }.launchIn(viewModelScope)
  }

  private fun observeForUserData(workspaceId: String, user: DomainLayerUsers.SKUser) {
    observeNewMessagesJob = useCaseObserveMessages
      .invoke(UseCaseWorkspaceChannelRequest(workspaceId = workspaceId, user.uuid))
      .launchIn(viewModelScope)
    useCaseObserveUsersJob = useCaseObserveUsers
      .invoke(workspaceId)
      .launchIn(viewModelScope)
    useCaseObserveChannelsJob = useCaseObserveChannels
      .invoke(workspaceId)
      .launchIn(viewModelScope)
  }

  private fun cancelJobIfWorkspaceChanged(workspaceId: String) {
    lastWorkspace?.let { lastWorkspace ->
      if (lastWorkspace != workspaceId) {
        observeNewMessagesJob?.cancel()
        useCaseObserveUsersJob?.cancel()
        useCaseObserveChannelsJob?.cancel()
      }
    }
  }
}
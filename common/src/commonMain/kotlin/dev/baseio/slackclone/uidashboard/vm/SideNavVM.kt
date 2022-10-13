package dev.baseio.slackclone.uidashboard.vm

import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseSetLastSelectedWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchWorkspaces
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ViewModel

class SideNavVM(
  private val useCaseFetchWorkspaces: UseCaseFetchWorkspaces,
  private val useCaseLastSelectedWorkspace: UseCaseSetLastSelectedWorkspace,
  private val userProfileDelegate: UserProfileDelegate
) : ViewModel(), UserProfileDelegate by userProfileDelegate {

  var workspacesFlow = MutableStateFlow(flow())
    private set

  init {
    getCurrentUser(viewModelScope)
  }

  fun flow(): Flow<List<DomainLayerWorkspaces.SKWorkspace>> {
    return useCaseFetchWorkspaces.invoke()
  }

  fun select(skWorkspace: DomainLayerWorkspaces.SKWorkspace) {
    viewModelScope.launch {
      useCaseLastSelectedWorkspace.invoke(skWorkspace)
    }
  }
}
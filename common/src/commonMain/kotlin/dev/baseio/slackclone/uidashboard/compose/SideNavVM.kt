package dev.baseio.slackclone.uidashboard.compose

import ViewModel
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseSetLastSelectedWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchWorkspaces
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SideNavVM(
  private val useCaseFetchWorkspaces: UseCaseFetchWorkspaces,
  private val useCaseLastSelectedWorkspace: UseCaseSetLastSelectedWorkspace
) : ViewModel() {
  var workspacesFlow = MutableStateFlow(flow())
    private set

  fun flow(): Flow<List<DomainLayerWorkspaces.SKWorkspace>> {
    return useCaseFetchWorkspaces.fetchWorkspaces()
  }

  fun select(skWorkspace: DomainLayerWorkspaces.SKWorkspace) {
    viewModelScope.launch {
      useCaseLastSelectedWorkspace.invoke(skWorkspace)
    }
  }
}
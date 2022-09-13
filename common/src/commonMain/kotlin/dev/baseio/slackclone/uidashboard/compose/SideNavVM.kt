package dev.baseio.slackclone.uidashboard.compose

import ViewModel
import dev.baseio.slackdomain.datasources.local.workspaces.SKDataSourceWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SideNavVM(private val skDataSourceWorkspaces: SKDataSourceWorkspaces) : ViewModel() {
  var workspacesFlow = MutableStateFlow(flow())
    private set

  fun flow(): Flow<List<DomainLayerWorkspaces.SKWorkspace>> {
    return skDataSourceWorkspaces.fetchWorkspaces()
  }

  fun select(skWorkspace: DomainLayerWorkspaces.SKWorkspace) {
    viewModelScope.launch {
      skDataSourceWorkspaces.setLastSelected(skWorkspace)
    }
  }
}
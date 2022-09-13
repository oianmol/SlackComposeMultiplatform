package dev.baseio.slackclone.uidashboard.compose

import ViewModel
import dev.baseio.slackdomain.datasources.local.workspaces.SKDataSourceWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SideNavVM(private val skDataSourceWorkspaces: SKDataSourceWorkspaces) : ViewModel() {
  var workspacesFlow = MutableStateFlow(flow())
    private set

  fun flow(): Flow<List<DomainLayerWorkspaces.SKWorkspace>> {
    return skDataSourceWorkspaces.fetchWorkspaces()
  }
}
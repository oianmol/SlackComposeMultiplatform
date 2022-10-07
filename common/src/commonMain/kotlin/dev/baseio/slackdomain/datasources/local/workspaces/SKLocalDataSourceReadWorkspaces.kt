package dev.baseio.slackdomain.datasources.local.workspaces

import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.flow.Flow

interface SKLocalDataSourceReadWorkspaces {
  suspend fun lastSelectedWorkspace(): DomainLayerWorkspaces.SKWorkspace?
  fun lastSelectedWorkspaceAsFlow(): Flow<DomainLayerWorkspaces.SKWorkspace>

  suspend fun setLastSelected(skWorkspace: DomainLayerWorkspaces.SKWorkspace)
  suspend fun workspacesCount(): Long
  suspend fun getWorkspace(uuid: String): DomainLayerWorkspaces.SKWorkspace?
  fun fetchWorkspaces(): Flow<List<DomainLayerWorkspaces.SKWorkspace>>
}
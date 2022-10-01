package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.slackdomain.datasources.local.workspaces.SKDataSourceWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow

class UseCaseGetSelectedWorkspace(private val skDataSourceWorkspaces: SKDataSourceWorkspaces) : BaseUseCase<DomainLayerWorkspaces.SKWorkspace?, Unit> {
  override suspend fun perform(): DomainLayerWorkspaces.SKWorkspace? {
    return skDataSourceWorkspaces.lastSelectedWorkspace()
  }

  override fun performStreaming(params: Unit): Flow<DomainLayerWorkspaces.SKWorkspace?> {
    return skDataSourceWorkspaces.lastSelectedWorkspaceAsFlow()
  }
}
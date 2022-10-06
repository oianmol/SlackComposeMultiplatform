package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceReadWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow

class UseCaseGetSelectedWorkspace(private val skLocalDataSourceReadWorkspaces: SKLocalDataSourceReadWorkspaces) : BaseUseCase<DomainLayerWorkspaces.SKWorkspace?, Unit> {
  override suspend fun perform(): DomainLayerWorkspaces.SKWorkspace? {
    return skLocalDataSourceReadWorkspaces.lastSelectedWorkspace()
  }

  override fun performStreaming(params: Unit): Flow<DomainLayerWorkspaces.SKWorkspace?> {
    return skLocalDataSourceReadWorkspaces.lastSelectedWorkspaceAsFlow()
  }
}
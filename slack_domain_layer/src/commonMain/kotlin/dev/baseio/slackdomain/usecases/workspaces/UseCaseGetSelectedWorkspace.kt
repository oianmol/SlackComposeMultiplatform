package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceReadWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.flow.Flow

class UseCaseGetSelectedWorkspace(private val skLocalDataSourceReadWorkspaces: SKLocalDataSourceReadWorkspaces) {
  suspend operator fun invoke(): DomainLayerWorkspaces.SKWorkspace? {
    return skLocalDataSourceReadWorkspaces.lastSelectedWorkspace()
  }

  fun invokeFlow(): Flow<DomainLayerWorkspaces.SKWorkspace?> {
    return skLocalDataSourceReadWorkspaces.lastSelectedWorkspaceAsFlow()
  }
}
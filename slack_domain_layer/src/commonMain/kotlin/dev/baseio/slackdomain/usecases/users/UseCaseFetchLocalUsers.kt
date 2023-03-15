package dev.baseio.slackdomain.usecases.users

import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow

class UseCaseFetchLocalUsers(
  private val SKLocalDataSourceUsers: SKLocalDataSourceUsers
) {
  operator fun invoke(workspaceId: String, search: String): Flow<List<DomainLayerUsers.SKUser>> {
    return SKLocalDataSourceUsers.getUsersByWorkspaceAndName(workspaceId, search)
  }

  operator fun invoke(workspaceId: String): Flow<List<DomainLayerUsers.SKUser>> {
    return SKLocalDataSourceUsers.getUsersByWorkspace(workspaceId)
  }
}
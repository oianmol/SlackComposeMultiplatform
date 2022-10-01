package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.datasources.local.users.SKDataSourceUsers
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.BaseUseCase

class UseCaseFetchUsers(private val SKDataSourceUsers: SKDataSourceUsers) :
  BaseUseCase<List<DomainLayerUsers.SKUser>, DomainLayerWorkspaces.SKWorkspace> {
  override suspend fun perform(workspace: DomainLayerWorkspaces.SKWorkspace): List<DomainLayerUsers.SKUser> {
    return SKDataSourceUsers.getUsers(workspace)
  }
}
package dev.baseio.slackdomain.usecases.users

import dev.baseio.slackdomain.datasources.local.users.SKDataSourceCreateUsers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.users.SKNetworkDataSourceReadUsers
import dev.baseio.slackdomain.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest

class UseCaseFetchAndSaveUsers(
  private val SKLocalDataSourceUsers: SKLocalDataSourceUsers,
  private val SKDataSourceCreateUsers: SKDataSourceCreateUsers,
  private val skNetworkDataSourceReadUsers: SKNetworkDataSourceReadUsers
) :
  BaseUseCase<List<DomainLayerUsers.SKUser>, String> {
  override fun performStreaming(params: String): Flow<List<DomainLayerUsers.SKUser>> {
    return skNetworkDataSourceReadUsers.fetchUsers(workspaceId = params).mapLatest { users ->
      SKDataSourceCreateUsers.saveUsers(users)
    }.flatMapLatest { _ ->
      SKLocalDataSourceUsers.getUsers(params)
    }
  }
}
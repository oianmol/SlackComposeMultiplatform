package dev.baseio.slackdomain.datasources.remote.users

import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow

interface SKNetworkDataSourceReadUsers {
  suspend fun fetchUsers(workspaceId: String): Result<List<DomainLayerUsers.SKUser>>
  fun listenToChangeInUsers(workspaceId: String): Flow<Pair<DomainLayerUsers.SKUser?, DomainLayerUsers.SKUser?>>
}
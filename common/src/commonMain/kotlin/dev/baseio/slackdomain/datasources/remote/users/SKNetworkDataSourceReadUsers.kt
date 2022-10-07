package dev.baseio.slackdomain.datasources.remote.users

import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow

interface SKNetworkDataSourceReadUsers {
  fun fetchUsers(workspaceId: String): Flow<List<DomainLayerUsers.SKUser>>
}
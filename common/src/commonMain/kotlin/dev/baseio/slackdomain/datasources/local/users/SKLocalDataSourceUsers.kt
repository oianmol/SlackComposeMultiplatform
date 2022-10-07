package dev.baseio.slackdomain.datasources.local.users

import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow

interface SKLocalDataSourceUsers {
  fun getUsers(workspace: String): Flow<List<DomainLayerUsers.SKUser>>
}
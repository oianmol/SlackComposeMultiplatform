package dev.baseio.slackdomain.usecases.users

import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow

class UseCaseFetchLocalUsers(
  private val SKLocalDataSourceUsers: SKLocalDataSourceUsers
) {
  fun performStreaming(params: String): Flow<List<DomainLayerUsers.SKUser>> {
    return SKLocalDataSourceUsers.getUsers(params)
  }
}
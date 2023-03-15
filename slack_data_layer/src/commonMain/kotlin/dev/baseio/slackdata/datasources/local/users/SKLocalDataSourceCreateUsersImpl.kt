package dev.baseio.slackdata.datasources.local.users

import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceWriteUsers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.withContext

class SKLocalDataSourceCreateUsersImpl(
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
  private val skLocalDataSourceUsers: SKLocalDataSourceUsers
) : SKLocalDataSourceWriteUsers {
  override suspend fun saveUsers(users: List<DomainLayerUsers.SKUser>) {
    withContext(coroutineDispatcherProvider.io) {
      users.forEach {
        skLocalDataSourceUsers.saveUser(it)
      }
    }
  }
}
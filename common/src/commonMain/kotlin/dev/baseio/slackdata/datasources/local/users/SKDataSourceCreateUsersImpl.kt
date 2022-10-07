package dev.baseio.slackdata.datasources.local.users

import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.users.SKDataSourceCreateUsers
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.withContext

class SKDataSourceCreateUsersImpl(
  private val slackDB: SlackDB,
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKDataSourceCreateUsers {
  override suspend fun saveUsers(users: List<DomainLayerUsers.SKUser>) {
    withContext(coroutineDispatcherProvider.io) {
      users.forEach {
        slackDB.slackDBQueries.insertUser(
          it.uuid,
          it.workspaceId,
          it.gender,
          it.name,
          it.location,
          it.email,
          it.username,
          it.userSince,
          it.phone,
          it.avatarUrl
        )
      }
    }
  }
}
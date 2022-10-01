package dev.baseio.slackdata.datasources.users

import database.SlackUser
import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.datasources.local.users.SKDataSourceUsers
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.withContext

class SKDataSourceUsersImpl constructor(
  private val slackDB: SlackDB,
  private val mapper: EntityMapper<DomainLayerUsers.SKUser, SlackUser>,
  private val coroutineMainDispatcherProvider: CoroutineDispatcherProvider
) :
  SKDataSourceUsers {
  override suspend fun getUsers(workspace: DomainLayerWorkspaces.SKWorkspace): List<DomainLayerUsers.SKUser> {
    return withContext(coroutineMainDispatcherProvider.io) {
      slackDB.slackDBQueries
        .selectAllUsers(workspace.uuid)
        .executeAsList()
        .map {
          mapper.mapToDomain(it)
        }
    }
  }
}
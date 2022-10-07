package dev.baseio.slackdata.datasources.local.users

import database.SlackUser
import dev.baseio.database.SlackDB
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SKLocalDataSourceUsersImpl(
  private val slackDB: SlackDB,
  private val mapper: EntityMapper<DomainLayerUsers.SKUser, SlackUser>
) :
  SKLocalDataSourceUsers {
  override fun getUsers(workspace: String): Flow<List<DomainLayerUsers.SKUser>> {
    return slackDB.slackDBQueries
      .selectAllUsers(workspace)
      .asFlow()
      .mapToList().map { slackUsers ->
        slackUsers.map { slackUser ->
          mapper.mapToDomain(slackUser)
        }
      }
  }
}
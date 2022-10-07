package dev.baseio.slackserver.data.impl

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import database.SkUser
import dev.baseio.SlackCloneDB
import dev.baseio.slackdata.protos.SKWorkspaceChannelRequest
import dev.baseio.slackserver.data.UsersDataSource
import kotlinx.coroutines.flow.Flow

class UsersDataSourceImpl(private val slackCloneDB: SlackCloneDB) : UsersDataSource {
  override fun saveUser(skUser: SkUser): SkUser {
    slackCloneDB.slackschemaQueries.insertUser(
      skUser.uuid,
      skUser.workspaceId,
      skUser.gender,
      skUser.name,
      skUser.location,
      skUser.email,
      skUser.username,
      skUser.userSince,
      skUser.phone,
      skUser.avatarUrl
    )
    return skUser
  }

  override fun getUsers(workspaceId: String): Flow<Query<SkUser>> {
    return slackCloneDB.slackschemaQueries
      .selectAllUsers(workspaceid = workspaceId)
      .asFlow()
  }

  override fun getUser(userId: String, workspaceId: String): SkUser? {
    return slackCloneDB.slackschemaQueries.getUserWithIdAndWorkspaceId(userId,workspaceId)
      .executeAsOneOrNull()
  }
}
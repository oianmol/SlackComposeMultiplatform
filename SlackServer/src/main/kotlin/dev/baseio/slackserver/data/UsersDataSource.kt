package dev.baseio.slackserver.data

import com.squareup.sqldelight.Query
import database.SkUser
import kotlinx.coroutines.flow.Flow

interface UsersDataSource {
  fun saveUser(skUser: SkUser): SkUser
  fun getUsers(workspaceId: String): Flow<Query<SkUser>>
  abstract fun getUser(userId: String, workspaceId: String): SkUser?
}
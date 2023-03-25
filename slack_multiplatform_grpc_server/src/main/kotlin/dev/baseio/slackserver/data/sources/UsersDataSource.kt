package dev.baseio.slackserver.data.sources

import dev.baseio.slackserver.data.models.SkUser
import kotlinx.coroutines.flow.Flow

interface UsersDataSource {
  suspend fun saveUser(skUser: SkUser): SkUser?
  fun getChangeInUserFor(workspaceId: String): Flow<Pair<SkUser?, SkUser?>>
  suspend fun getUsers(workspaceId: String): List<SkUser>
  suspend fun getUser(userId: String, workspaceId: String): SkUser?
  suspend fun updateUser(request: SkUser): SkUser?
  suspend fun getUserWithEmailId(emailId: String, workspaceId: String): SkUser?
  suspend fun getUserWithUsername(userName: String?, workspaceId: String): SkUser?
  suspend fun getUserWithUserId(userId: String,workspaceId: String):SkUser?
}
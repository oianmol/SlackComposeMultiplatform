package dev.baseio.slackdomain.usecases.users

import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.users.SKNetworkDataSourceReadUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UseCaseFetchAndUpdateChangeInUsers(
  private val skNetworkDataSourceMessages: SKNetworkDataSourceReadUsers,
  private val skLocalDataSourceUsers: SKLocalDataSourceUsers
) {
  operator fun invoke(workspaceId: String): Flow<Unit> {
    return skNetworkDataSourceMessages.listenToChangeInUsers(workspaceId)
      .map { messageChangeSnapshot ->
        messageChangeSnapshot.second?.let {
          skLocalDataSourceUsers.saveUser(it)
        }
        Unit
      }
      .catch {
        // TODO tell upstream of exceptions if any
      }
  }
}

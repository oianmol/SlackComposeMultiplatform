package dev.baseio.slackdomain.usecases.users

import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceWriteUsers
import dev.baseio.slackdomain.datasources.remote.users.SKNetworkDataSourceReadUsers

class UseCaseFetchAndSaveUsers(
    private val skLocalDataSourceWriteUsers: SKLocalDataSourceWriteUsers,
    private val skNetworkDataSourceReadUsers: SKNetworkDataSourceReadUsers
) {
    suspend operator fun invoke(params: String) {
        return kotlin.runCatching {
            val users = skNetworkDataSourceReadUsers.fetchUsers(workspaceId = params).getOrThrow()
            skLocalDataSourceWriteUsers.saveUsers(users)
        }.run {
            when {
                isFailure -> {
                    // TODO update upstream of errors!
                }
            }
        }
    }
}
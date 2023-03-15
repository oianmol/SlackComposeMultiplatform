package dev.baseio.slackdomain.usecases.auth

import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.auth.SKAuthNetworkDataSource
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class UseCaseFetchAndSaveCurrentUser(private val skAuthNetworkDataSource: SKAuthNetworkDataSource,
                                     private val skLocalDataSourceUsers: SKLocalDataSourceUsers) {
  suspend operator fun invoke(): Result<DomainLayerUsers.SKUser> {
    return skAuthNetworkDataSource.getLoggedInUser().also {
      skLocalDataSourceUsers.saveLoggedInUser(it.getOrNull())
    }
  }
}

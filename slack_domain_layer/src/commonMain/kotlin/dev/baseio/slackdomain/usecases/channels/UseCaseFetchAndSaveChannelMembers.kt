package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannelMembers

class UseCaseFetchAndSaveChannelMembers(
  private val networkSource: SKNetworkDataSourceReadChannelMembers,
  private val localSource: SKLocalDataSourceChannelMembers
) {
  suspend operator fun invoke(useCaseWorkspaceChannelRequest: UseCaseWorkspaceChannelRequest) {
    networkSource.fetchChannelMembers(useCaseWorkspaceChannelRequest).mapCatching {
      localSource.save(it)
    }
  }
}
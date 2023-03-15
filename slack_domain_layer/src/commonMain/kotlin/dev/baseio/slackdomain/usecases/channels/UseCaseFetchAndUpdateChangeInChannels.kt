package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceCreateChannels
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannels
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UseCaseFetchAndUpdateChangeInChannels(
  private val readChannelsSource: SKNetworkDataSourceReadChannels,
  private val localSourceChannel: SKLocalDataSourceCreateChannels
) {
  operator fun invoke(workspaceId: String): Flow<Unit> {
    return readChannelsSource.listenToChangeInChannels(workspaceId)
      .map { messageChangeSnapshot ->
        messageChangeSnapshot.second?.let {
          localSourceChannel.saveChannel(it)
        }
        Unit
      }
      .catch {
        // TODO tell upstream of exceptions if any
      }
  }
}

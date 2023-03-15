package dev.baseio.slackdomain.datasources.remote.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.flow.Flow

interface SKNetworkDataSourceReadChannels {
  suspend fun fetchChannels(workspaceId: String, offset: Int, limit: Int): Result<List<DomainLayerChannels.SKChannel>>
  fun listenToChangeInChannels(workspaceId: String): Flow<Pair<DomainLayerChannels.SKChannel?, DomainLayerChannels.SKChannel?>>
}
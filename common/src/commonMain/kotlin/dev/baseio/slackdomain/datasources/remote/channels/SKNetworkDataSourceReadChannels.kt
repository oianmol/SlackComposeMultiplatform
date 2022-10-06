package dev.baseio.slackdomain.datasources.remote.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.flow.Flow

interface SKNetworkDataSourceReadChannels {
  fun fetchChannels(workspaceId: String): Flow<List<DomainLayerChannels.SKChannel>>
}
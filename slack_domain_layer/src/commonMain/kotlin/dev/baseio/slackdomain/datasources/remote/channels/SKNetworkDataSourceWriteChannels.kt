package dev.baseio.slackdomain.datasources.remote.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels

interface SKNetworkDataSourceWriteChannels {
  suspend fun createChannel(params: DomainLayerChannels.SKChannel): Result<DomainLayerChannels.SKChannel>
}
package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceCreateChannels
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceWriteChannels
import dev.baseio.slackdomain.usecases.BaseUseCase

class UseCaseCreateChannel(
  private val SKLocalDataSourceCreateChannels: SKLocalDataSourceCreateChannels,
  private val skNetworkDataSourceWriteChannels: SKNetworkDataSourceWriteChannels
) :
  BaseUseCase<DomainLayerChannels.SKChannel, DomainLayerChannels.SKChannel> {
  override suspend fun perform(params: DomainLayerChannels.SKChannel): DomainLayerChannels.SKChannel {
    val channel = skNetworkDataSourceWriteChannels.createChannel(params)
    return SKLocalDataSourceCreateChannels.saveChannel(channel)
  }
}
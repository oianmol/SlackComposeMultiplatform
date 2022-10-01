package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceCreateChannels
import dev.baseio.slackdomain.usecases.BaseUseCase

class UseCaseCreateChannel(private val SKDataSourceCreateChannels: SKDataSourceCreateChannels) :
  BaseUseCase<DomainLayerChannels.SKChannel, DomainLayerChannels.SKChannel> {
  override suspend fun perform(params: DomainLayerChannels.SKChannel): DomainLayerChannels.SKChannel? {
    return SKDataSourceCreateChannels.saveChannel(params)
  }
}
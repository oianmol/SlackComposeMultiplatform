package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.BaseUseCase

class UseCaseGetChannel(private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels) :
  BaseUseCase<DomainLayerChannels.SKChannel, UseCaseChannelRequest> {
  override suspend fun perform(params: UseCaseChannelRequest): DomainLayerChannels.SKChannel? {
    return skLocalDataSourceReadChannels.getChannel(
      UseCaseChannelRequest(
        workspaceId = params.workspaceId,
        uuid = params.uuid
      )
    )
  }
}

data class UseCaseChannelRequest(val workspaceId: String, val uuid: String)
package dev.baseio.slackdomain.datasources.remote.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest

interface SKNetworkDataSourceReadChannelMembers {
  suspend fun fetchChannelMembers(request: UseCaseWorkspaceChannelRequest): Result<List<DomainLayerChannels.SkChannelMember>>
}
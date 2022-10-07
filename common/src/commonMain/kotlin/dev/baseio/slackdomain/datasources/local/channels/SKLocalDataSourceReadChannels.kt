package dev.baseio.slackdomain.datasources.local.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.flow.Flow

interface SKLocalDataSourceReadChannels {
  suspend fun channelCount(workspaceId: String): Long
  suspend fun getChannel(request: UseCaseChannelRequest): DomainLayerChannels.SKChannel?
  fun fetchChannels(workspaceId: String): Flow<List<DomainLayerChannels.SKChannel>>
  fun fetchChannelsOrByName(workspaceId: String, params: String?): Flow<List<DomainLayerChannels.SKChannel>>
  fun getChannelById(workspaceId: String,uuid: String):DomainLayerChannels.SKChannel?
}
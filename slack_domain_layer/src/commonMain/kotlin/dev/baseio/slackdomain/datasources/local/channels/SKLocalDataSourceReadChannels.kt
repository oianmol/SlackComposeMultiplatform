package dev.baseio.slackdomain.datasources.local.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import kotlinx.coroutines.flow.Flow

interface SKLocalDataSourceReadChannels {
  suspend fun channelCount(workspaceId: String): Long
  suspend fun getChannel(request: UseCaseWorkspaceChannelRequest): DomainLayerChannels.SKChannel?
  fun fetchAllChannels(workspaceId: String): Flow<List<DomainLayerChannels.SKChannel>>
  fun fetchChannelsOrByName(workspaceId: String, params: String?): Flow<List<DomainLayerChannels.SKChannel>>
  suspend fun getChannelById(workspaceId: String, uuid: String): DomainLayerChannels.SKChannel?
  suspend fun getChannelByReceiverId(workspaceId: String, uuid: String): DomainLayerChannels.SKChannel.SkDMChannel?
  suspend fun getChannelByReceiverIdAndSenderId(workspaceId: String, receiverId: String, senderId: String): DomainLayerChannels.SKChannel.SkDMChannel?
  suspend fun getChannelByChannelId(channelId:String): DomainLayerChannels.SKChannel?
}
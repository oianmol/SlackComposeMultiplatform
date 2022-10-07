package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannelLastMessage
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.BaseUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class UseCaseFetchRecentChannels(private val skDataSourceChannelLastMessage: SKDataSourceChannelLastMessage) :
  BaseUseCase<List<DomainLayerChannels.SKChannel>, String> {
  override fun performStreaming(params: String): Flow<List<DomainLayerChannels.SKChannel>> {
    return skDataSourceChannelLastMessage.fetchChannelsWithLastMessage(params)
      .mapLatest { skLastMessageList ->
        skLastMessageList.map { skLastMessage -> skLastMessage.channel }
      }
  }
}
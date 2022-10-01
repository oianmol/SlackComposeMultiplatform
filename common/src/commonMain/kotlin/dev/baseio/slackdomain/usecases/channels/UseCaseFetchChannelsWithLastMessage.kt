package dev.baseio.slackdomain.usecases.channels


import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannelLastMessage
import dev.baseio.slackdomain.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow

class UseCaseFetchChannelsWithLastMessage(private val SKDataSourceChannelLastMessage: SKDataSourceChannelLastMessage) :
  BaseUseCase<List<DomainLayerMessages.SKLastMessage>, String> {

  override fun performStreaming(params: String): Flow<List<DomainLayerMessages.SKLastMessage>> {
    return SKDataSourceChannelLastMessage.fetchChannelsWithLastMessage(params)
  }

}
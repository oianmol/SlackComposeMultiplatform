package dev.baseio.slackdomain.usecases.channels


import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelLastMessage
import kotlinx.coroutines.flow.Flow

class UseCaseFetchChannelsWithLastMessage(
  private val SKLocalDataSourceChannelLastMessage: SKLocalDataSourceChannelLastMessage,
) {
  operator fun invoke(workspaceId: String): Flow<List<DomainLayerMessages.SKLastMessage>> {
    return SKLocalDataSourceChannelLastMessage.fetchChannelsWithLastMessage(workspaceId = workspaceId)
  }

}
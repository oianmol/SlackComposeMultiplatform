package dev.baseio.slackdomain.usecases.chat


import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.messages.SKDataSourceMessages
import dev.baseio.slackdomain.usecases.BaseUseCase
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.flow.Flow

class UseCaseFetchMessages(private val SKDataSourceMessages: SKDataSourceMessages) :
  BaseUseCase<List<DomainLayerMessages.SKMessage>, UseCaseChannelRequest> {
  override fun performStreaming(request: UseCaseChannelRequest): Flow<List<DomainLayerMessages.SKMessage>> {
    return SKDataSourceMessages.fetchMessages(request.workspaceId, userId = request.uuid)
  }
}

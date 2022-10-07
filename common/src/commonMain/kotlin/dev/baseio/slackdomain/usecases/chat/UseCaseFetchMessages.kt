package dev.baseio.slackdomain.usecases.chat


import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.usecases.BaseUseCase
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class UseCaseFetchMessages(
  private val SKLocalDataSourceMessages: SKLocalDataSourceMessages,
  private val skNetworkDataSourceMessages: SKNetworkDataSourceMessages
) :
  BaseUseCase<List<DomainLayerMessages.SKMessage>, UseCaseChannelRequest> {
  override fun performStreaming(request: UseCaseChannelRequest): Flow<List<DomainLayerMessages.SKMessage>> {
    return skNetworkDataSourceMessages.getMessages(request).flatMapLatest { messages ->
      messages.forEach {
        SKLocalDataSourceMessages.saveMessage(it)
      }
      SKLocalDataSourceMessages.fetchLocalMessages(request.workspaceId, userId = request.uuid)
    }
  }
}

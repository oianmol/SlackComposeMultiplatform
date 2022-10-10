package dev.baseio.slackdomain.usecases.chat


import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.usecases.BaseUseCase
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.flow.*

class UseCaseFetchMessages(
  private val SKLocalDataSourceMessages: SKLocalDataSourceMessages,
  private val skNetworkDataSourceMessages: SKNetworkDataSourceMessages,
  private val skLocalDataSourceUsers: SKLocalDataSourceUsers
) :
  BaseUseCase<List<DomainLayerMessages.SKMessage>, UseCaseChannelRequest> {
  override fun performStreaming(request: UseCaseChannelRequest): Flow<List<DomainLayerMessages.SKMessage>> {
    return skNetworkDataSourceMessages.getMessages(request)
      .flatMapLatest { messages ->
        messages.forEach {
          skLocalDataSourceUsers.saveUser(it.senderInfo)
          SKLocalDataSourceMessages.saveMessage(it)
        }
        SKLocalDataSourceMessages.fetchLocalMessages(request.workspaceId, userId = request.uuid)
      }
      .catch {
        emitAll(SKLocalDataSourceMessages.fetchLocalMessages(request.workspaceId, userId = request.uuid))
      }.mapLatest { messages -> // TODO fix this ? should we read
        messages.map { message ->
          val user = skLocalDataSourceUsers.getUser(message.workspaceId, message.sender)
          message.senderInfo = user
          message
        }
      }
  }
}

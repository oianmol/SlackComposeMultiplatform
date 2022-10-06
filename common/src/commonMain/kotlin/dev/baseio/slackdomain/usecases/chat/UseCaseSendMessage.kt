package dev.baseio.slackdomain.usecases.chat

import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.usecases.BaseUseCase

class UseCaseSendMessage(
  private val SKLocalDataSourceMessages: SKLocalDataSourceMessages,
  private val skNetworkDataSourceMessages: SKNetworkDataSourceMessages
) :
  BaseUseCase<DomainLayerMessages.SKMessage, DomainLayerMessages.SKMessage> {
  override suspend fun perform(params: DomainLayerMessages.SKMessage): DomainLayerMessages.SKMessage {
    // TODO send first to local and then to backend and work with sync flags ?
    val message = skNetworkDataSourceMessages.sendMessage(params)
    return SKLocalDataSourceMessages.saveMessage(message)
  }
}

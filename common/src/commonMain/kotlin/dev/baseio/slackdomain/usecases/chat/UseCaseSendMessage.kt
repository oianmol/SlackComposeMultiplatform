package dev.baseio.slackdomain.usecases.chat

import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.messages.SKDataSourceMessages
import dev.baseio.slackdomain.usecases.BaseUseCase

class UseCaseSendMessage(private val SKDataSourceMessages: SKDataSourceMessages) :
  BaseUseCase<DomainLayerMessages.SKMessage, DomainLayerMessages.SKMessage> {
  override suspend fun perform(params: DomainLayerMessages.SKMessage): DomainLayerMessages.SKMessage {
    return SKDataSourceMessages.sendMessage(params)
  }
}

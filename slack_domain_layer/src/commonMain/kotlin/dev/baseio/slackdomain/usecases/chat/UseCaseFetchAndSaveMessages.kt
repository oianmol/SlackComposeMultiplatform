package dev.baseio.slackdomain.usecases.chat

import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest

class UseCaseFetchAndSaveMessages(
  private val skLocalDataSourceMessages: SKLocalDataSourceMessages,
  private val skNetworkDataSourceMessages: SKNetworkDataSourceMessages
) {
  suspend operator fun invoke(request: UseCaseWorkspaceChannelRequest): List<DomainLayerMessages.SKMessage> {
    return kotlin.run {
      skNetworkDataSourceMessages.fetchMessages(request).getOrThrow().map { skMessage ->
        skLocalDataSourceMessages.saveMessage(skMessage)
        skMessage
      }
    }
  }
}

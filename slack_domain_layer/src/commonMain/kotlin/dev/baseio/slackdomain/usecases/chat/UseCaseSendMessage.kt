package dev.baseio.slackdomain.usecases.chat

import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.messages.SKLocalDataSourceMessages
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class UseCaseSendMessage(
    private val SKLocalDataSourceMessages: SKLocalDataSourceMessages,
    private val skNetworkDataSourceMessages: SKNetworkDataSourceMessages,

    ) {
    suspend operator fun invoke(
        params: DomainLayerMessages.SKMessage,
        publicKey: dev.baseio.slackdomain.model.users.DomainLayerUsers.SKSlackKey
    ): DomainLayerMessages.SKMessage {
        val message =
            skNetworkDataSourceMessages.sendMessage(
                params, publicKey
            )
        return SKLocalDataSourceMessages.saveMessage(
            message
        )
    }

    suspend fun deleteMessage(
        params: DomainLayerMessages.SKMessage,
        publicKey: DomainLayerUsers.SKSlackKey
    ): DomainLayerMessages.SKMessage {
        val message =
            skNetworkDataSourceMessages.deleteMessage(
                params, publicKey
            )
        return SKLocalDataSourceMessages.saveMessage(
            message
        )
    }
}

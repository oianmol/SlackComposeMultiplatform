package dev.baseio.slackdomain.datasources.local.messages

import dev.baseio.slackdomain.model.message.DomainLayerMessages

interface IMessageDecrypter {
    suspend fun decrypted(message: DomainLayerMessages.SKMessage): Result<DomainLayerMessages.SKMessage>
}

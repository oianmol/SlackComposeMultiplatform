package dev.baseio.slackdata.datasources.local.messages

import dev.baseio.security.*
import dev.baseio.slackdata.datasources.local.channels.loggedInUser
import dev.baseio.slackdomain.datasources.IDataDecryptor
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.datasources.local.messages.IMessageDecrypter
import dev.baseio.slackdomain.model.message.DomainLayerMessages

class IMessageDecrypterImpl(
    private val skKeyValueData: SKLocalKeyValueSource,
    private val iDataDecrypter: IDataDecryptor,
    private val skLocalDataSourceChannelMembers: SKLocalDataSourceChannelMembers,
) : IMessageDecrypter {
    override suspend fun decrypted(message: DomainLayerMessages.SKMessage): DomainLayerMessages.SKMessage? {
        val user = skKeyValueData.loggedInUser(message.workspaceId)
        val capillary =
            CapillaryInstances.getInstance(user?.email!!)
        return skLocalDataSourceChannelMembers.getChannelPrivateKeyForMe(
            message.workspaceId,
            message.channelId,
            user.uuid
        ).map { it.channelEncryptedPrivateKey }
            .firstNotNullOfOrNull { safeChannelEncryptedPrivateKey ->
                kotlin.runCatching {
                    capillary.decrypt(
                        EncryptedData(
                            safeChannelEncryptedPrivateKey.first,
                            safeChannelEncryptedPrivateKey.second
                        ),
                        capillary.privateKey()
                    )
                }.getOrNull()
            }?.let { bytes ->
                finalMessageAfterDecryption(
                    message,
                    bytes
                )
            }
    }

    private fun finalMessageAfterDecryption(
        skLastMessage: DomainLayerMessages.SKMessage,
        privateKeyBytes: ByteArray
    ): DomainLayerMessages.SKMessage {
        var messageFinal = skLastMessage
        runCatching {
            messageFinal =
                messageFinal.copy(
                    decodedMessage = iDataDecrypter.decrypt(
                        Pair(
                            messageFinal.messageFirst,
                            messageFinal.messageSecond
                        ),
                        privateKeyBytes = privateKeyBytes
                    )
                        .decodeToString()
                )
        }.exceptionOrNull()?.let {
            it.printStackTrace()
            messageFinal =
                messageFinal.copy(
                    decodedMessage = it.message.toString()
                )
        }
        return messageFinal
    }
}

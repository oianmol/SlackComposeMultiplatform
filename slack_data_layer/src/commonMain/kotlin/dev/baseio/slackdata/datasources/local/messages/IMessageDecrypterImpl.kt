package dev.baseio.slackdata.datasources.local.messages

import dev.baseio.security.*
import dev.baseio.slackdomain.datasources.local.messages.IMessageDecrypter
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdata.datasources.local.channels.loggedInUser
import dev.baseio.slackdomain.datasources.IDataDecryptor
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers

class IMessageDecrypterImpl(
    private val skKeyValueData: SKLocalKeyValueSource,
    private val iDataDecrypter: IDataDecryptor,
    private val skLocalDataSourceChannelMembers: SKLocalDataSourceChannelMembers,
) : IMessageDecrypter {
    override suspend fun decrypted(message: DomainLayerMessages.SKMessage): DomainLayerMessages.SKMessage? {
        val capillary =
            CapillaryInstances.getInstance(skKeyValueData.loggedInUser(message.workspaceId).email!!)
        return skLocalDataSourceChannelMembers.getChannelPrivateKeyForMe(
            message.workspaceId,
            message.channelId,
            skKeyValueData.loggedInUser(message.workspaceId).uuid
        )?.channelEncryptedPrivateKey?.let { safeChannelEncryptedPrivateKey ->
            capillary.decrypt(
                EncryptedData(
                    safeChannelEncryptedPrivateKey.first,
                    safeChannelEncryptedPrivateKey.second
                ), capillary.privateKey()
            )
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
                        ), privateKeyBytes = privateKeyBytes
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


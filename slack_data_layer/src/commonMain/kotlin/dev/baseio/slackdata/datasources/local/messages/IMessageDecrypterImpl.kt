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
    override suspend fun decrypted(message: DomainLayerMessages.SKMessage): Result<DomainLayerMessages.SKMessage> {
        return kotlin.runCatching {
            val privateKey = usersDecryptedPrivateKey(
                message.workspaceId,
                message.channelId,
            )
            val decodedMessage = privateKey?.let {
                iDataDecrypter.decrypt(
                    Pair(
                        message.messageFirst,
                        message.messageSecond
                    ),
                    privateKeyBytes = it
                ).decodeToString()
            } ?: throw Exception("Private key not available for channel member ?")
            message.copy(decodedMessage = decodedMessage)
        }
    }

    private suspend fun usersDecryptedPrivateKey(
        workspaceId: String,
        channelId: String,
    ): ByteArray? {
        val user = skKeyValueData.loggedInUser(workspaceId)
        //TODO the user should always have uuid and email, fix this!
        val capillary = user?.email?.let { CapillaryInstances.getInstance(it) }
        val safeChannelEncryptedPrivateKey =
            user?.uuid?.let {
                skLocalDataSourceChannelMembers.getChannelPrivateKeyForMe(
                    workspaceId,
                    channelId,
                    it
                )?.channelEncryptedPrivateKey
            }
        return capillary?.decrypt(
            EncryptedData(
                safeChannelEncryptedPrivateKey!!.first,
                safeChannelEncryptedPrivateKey.second
            ),
            capillary.privateKey()
        )
    }
}

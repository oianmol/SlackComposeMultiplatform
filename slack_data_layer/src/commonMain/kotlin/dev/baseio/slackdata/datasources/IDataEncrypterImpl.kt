package dev.baseio.slackdata.datasources

import dev.baseio.security.CapillaryEncryption
import dev.baseio.slackdomain.datasources.IDataEncrypter
import dev.baseio.security.toPublicKey
import dev.baseio.slackdata.datasources.remote.channels.toDomainSKEncryptedMessage
import dev.baseio.slackdata.toSKEncryptedMessage
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class IDataEncrypterImpl : IDataEncrypter {
    override fun encrypt(
        byteArray: ByteArray,
        publicKeyBytes: ByteArray
    ): DomainLayerUsers.SKEncryptedMessage {
        val encrypted = CapillaryEncryption.encrypt(
            byteArray, publicKeyBytes.toPublicKey()
        )
        return DomainLayerUsers.SKEncryptedMessage(encrypted.first, encrypted.second)
    }
}
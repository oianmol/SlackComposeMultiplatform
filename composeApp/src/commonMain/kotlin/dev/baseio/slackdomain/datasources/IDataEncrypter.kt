package dev.baseio.slackdomain.datasources

import dev.baseio.slackdomain.model.users.DomainLayerUsers

interface IDataEncrypter {
    fun encrypt(byteArray: ByteArray, publicKeyBytes: ByteArray): DomainLayerUsers.SKEncryptedMessage
}

interface IDataDecryptor {
    fun decrypt(byteArray: Pair<String, String>, privateKeyBytes: ByteArray): ByteArray
}

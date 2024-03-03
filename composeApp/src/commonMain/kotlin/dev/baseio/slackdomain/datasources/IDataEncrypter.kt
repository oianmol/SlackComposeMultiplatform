package dev.baseio.slackdomain.datasources

import dev.baseio.slackdomain.model.users.DomainLayerUsers

interface IDataEncrypter {
    suspend fun encrypt(byteArray: ByteArray, publicKeyBytes: ByteArray): DomainLayerUsers.SKEncryptedMessage
}

interface IDataDecryptor {
    suspend fun decrypt(byteArray: Pair<String, String>, privateKeyBytes: ByteArray): ByteArray
}

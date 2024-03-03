package dev.baseio.security

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

actual object CapillaryEncryption {

    actual suspend fun encrypt(
        plaintext: ByteArray,
        publicKey: PublicKey,
    ): EncryptedData {
        TODO("Pair(symmetricKeyCiphertext.base64(), payloadCiphertext.base64())")
    }

    actual suspend fun decrypt(
        encryptedData: EncryptedData,
        privateKey: PrivateKey,
    ): ByteArray {
        TODO("")
        /*return CryptoChaCha20.decrypt(
            encryptedData.second.frombase64()!!,
            CryptoChaCha20
                .secretFrom(symmetricKeyBytes)
        )*/
    }
}

@OptIn(ExperimentalEncodingApi::class)
private fun String.frombase64(): ByteArray {
    return Base64.decode(this)
}

@OptIn(ExperimentalEncodingApi::class)
private fun ByteArray.base64(): String {
    return Base64.encode(this)
}

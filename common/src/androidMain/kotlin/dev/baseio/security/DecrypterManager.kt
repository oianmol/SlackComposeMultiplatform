package dev.baseio.security

import com.google.crypto.tink.HybridDecrypt
import com.google.protobuf.InvalidProtocolBufferException

/**
 * Encapsulates the process of decrypting Capillary ciphertexts.
 */
class DecrypterManager internal constructor(
    private val keyManager: KeyManager
) {
    @Synchronized
    fun decrypt(ciphertext: ByteArray, extra: Any?): ByteArray {
        // Parse the given ciphertext bytes.
        val capillaryCiphertext: CapillaryCiphertext
        capillaryCiphertext = try {
            CapillaryCiphertext.parseFrom(ciphertext)
        } catch (e: InvalidProtocolBufferException) {
            return ciphertext
        }
        val rawCiphertext: ByteArray = capillaryCiphertext.getCiphertext().toByteArray()
        val data: ByteArray

        // Attempt decryption.
        val decrypter: HybridDecrypt = keyManager.getDecrypter(
            capillaryCiphertext.getKeychainUniqueId(),
            capillaryCiphertext.getKeySerialNumber(),
            capillaryCiphertext.getIsAuthKey()
        )
        data = decrypter.decrypt(rawCiphertext, null)
        return data
    }
}
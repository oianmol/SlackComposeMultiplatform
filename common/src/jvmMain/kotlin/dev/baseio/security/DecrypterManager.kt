package dev.baseio.security

import com.google.crypto.tink.HybridDecrypt
import dev.baseio.slackdata.securepush.KMSlackCiphertext

/**
 * Encapsulates the process of decrypting Capillary ciphertexts.
 */
actual class DecrypterManager internal actual constructor(private val keyManager: KeyManager) {

    actual fun decrypt(ciphertext: KMSlackCiphertext): ByteArray {
        val rawCiphertext: ByteArray = ciphertext.ciphertextList.map { it.byte.toByte() }.toByteArray()
        val data: ByteArray

        // Attempt decryption.
        val decrypter: HybridDecrypt = keyManager.getDecrypter(
            ciphertext.keychainuniqueid,
            ciphertext.keyserialnumber,
            ciphertext.isauthkey
        )
        data = decrypter.decrypt(rawCiphertext, null)
        return data
    }
}
package dev.baseio.security

import dev.baseio.slackdata.securepush.KMSlackCiphertext

/**
 * Encapsulates the process of decrypting Capillary ciphertexts.
 */
actual class DecrypterManager internal actual constructor(keyManager: KeyManager) {
    actual fun decrypt(ciphertext: KMSlackCiphertext): ByteArray {
        TODO("Not yet implemented")
    }
}
package dev.baseio.security

import dev.baseio.slackdata.securepush.KMSlackCiphertext

/**
 * Encapsulates the process of decrypting Capillary ciphertexts.
 */
expect class DecrypterManager internal constructor(keyManager: KeyManager) {
    fun decrypt(ciphertext: KMSlackCiphertext): ByteArray
}
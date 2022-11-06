package dev.baseio.security

import com.google.protobuf.ByteString
import dev.baseio.slackdata.securepush.KMKeyAlgorithm
import dev.baseio.slackdata.securepush.KMSecureNotification
import dev.baseio.slackdata.securepush.kmSecureNotification
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore

/**
 * Contains common helper functions used by the Android classes.
 */
object Utils {
    const val KEYSTORE_JVM = "pkcs12"

    /**
     * Initializes the library.
     */
    fun initialize() {
        try {
            Capillary.initialize()
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Creates a demo notification message and returns its serialized bytes.
     *
     * @param title the title of the notification.
     * @param keyAlgorithm the algorithm used to encrypt the notification.
     * @param isAuthKey whether the notification was encrypted using an authenticated key.
     * @return serialized notification bytes.
     */
    fun createSecureMessageBytes(
        messageTitle: String?, keyAlgorithm: KMKeyAlgorithm?, isAuthKey: Boolean
    ): ByteString {
        return kmSecureNotification {
            id = System.currentTimeMillis().toInt()
            title = messageTitle
            body = java.lang.String.format("Algorithm=%s, IsAuth=%s", keyAlgorithm, isAuthKey)
        }.toByteString()
    }

    /**
     * Creates a Capillary key manager for the specified key algorithm.
     */
    @Throws(IOException::class, GeneralSecurityException::class)
    fun getKeyManager(algorithm: KMKeyAlgorithm?): KeyManager {
        return when (algorithm) {
            KMKeyAlgorithm.RSA_ECDSA -> {
                this.javaClass.getResourceAsStream("sender_verification_key.dat").use { senderVerificationKey ->
                    return RsaEcdsaKeyManager.getInstance(
                        RSA_ECDSA_KEYCHAIN_ID, senderVerificationKey
                    )
                }
            }

            KMKeyAlgorithm.WEB_PUSH -> WebPushKeyManager.getInstance(WEB_PUSH_KEYCHAIN_ID)
            else -> throw IllegalArgumentException("unsupported key algorithm")
        }
    }

    fun loadKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(KEYSTORE_JVM)
        try {
            keyStore.load(null)
        } catch (e: IOException) {
            throw GeneralSecurityException("unable to load keystore", e)
        }
        return keyStore
    }
}

fun KMSecureNotification.toByteString(): ByteString {
    TODO("Not yet implemented")
}

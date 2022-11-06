package dev.baseio.security

import android.content.Context
import com.google.crypto.tink.HybridDecrypt
import dev.baseio.protoextensions.toByteArray
import dev.baseio.slackdata.protos.kmSKByteArrayElement
import dev.baseio.slackdata.securepush.kmWrappedRsaEcdsaPublicKey
import java.io.IOException
import java.io.InputStream
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.PrivateKey

/**
 * An implementation of [KeyManager] that supports RSA-ECDSA keys.
 */
actual class RsaEcdsaKeyManager(
    chainId: String,
    senderVerificationKey: InputStream
) : KeyManager() {
    private val keychainId = KEY_CHAIN_ID_PREFIX + chainId
    private var keyStore: KeyStore
    private var senderVerifier: com.google.crypto.tink.PublicKeyVerify

    init {
        val verificationKeyHandle: com.google.crypto.tink.KeysetHandle = com.google.crypto.tink.CleartextKeysetHandle
            .read(com.google.crypto.tink.BinaryKeysetReader.withInputStream(senderVerificationKey))
        senderVerifier = com.google.crypto.tink.signature.PublicKeyVerifyFactory.getPrimitive(verificationKeyHandle)
        keyStore = Utils.loadKeyStore()
    }

    private fun updateSenderVerifier(senderVerificationKey: InputStream) {
        val verificationKeyHandle: com.google.crypto.tink.KeysetHandle = com.google.crypto.tink.CleartextKeysetHandle
            .read(com.google.crypto.tink.BinaryKeysetReader.withInputStream(senderVerificationKey))
        senderVerifier = com.google.crypto.tink.signature.PublicKeyVerifyFactory.getPrimitive(verificationKeyHandle)
    }

    actual fun rawGenerateKeyPair(isAuth: Boolean) {
        AndroidKeyStoreRsaUtils.generateKeyPair(keychainId, isAuth)
    }

    actual fun rawGetPublicKey(isAuth: Boolean): ByteArray {
        val publicKeyBytes: ByteArray = AndroidKeyStoreRsaUtils.getPublicKey(keyStore, keychainId, isAuth).encoded
        return kmWrappedRsaEcdsaPublicKey {
            padding = AndroidKeyStoreRsaUtils.compatibleRsaPadding.name
            keybytesList.addAll(publicKeyBytes.map {
                kmSKByteArrayElement {
                    byte = it.toInt()
                }
            })
        }.toByteArray()
    }

    actual fun decrypt(cipherText: ByteArray, contextInfo: ByteArray?): ByteArray? {
        return rawGetDecrypter(false).decrypt(cipherText, contextInfo)
    }

    fun rawGetDecrypter(isAuth: Boolean): HybridDecrypt {
        val recipientPrivateKey: PrivateKey = AndroidKeyStoreRsaUtils.getPrivateKey(keyStore, keychainId, isAuth)
        return RsaEcdsaHybridDecrypt.Builder()
            .withRecipientPrivateKey(recipientPrivateKey)
            .withSenderVerifier(senderVerifier)
            .withPadding(AndroidKeyStoreRsaUtils.compatibleRsaPadding)
            .build()
    }

    actual fun rawDeleteKeyPair(isAuth: Boolean) {
        AndroidKeyStoreRsaUtils.deleteKeyPair(keyStore, keychainId, isAuth)
    }

    companion object {
        // This prefix should be unique to each implementation of KeyManager.
        private const val KEY_CHAIN_ID_PREFIX = "rsa_ecdsa_"
        private val instances: HashMap<String, RsaEcdsaKeyManager> = HashMap()

        /**
         * Returns the singleton [RsaEcdsaKeyManager] instance for the given keychain ID.
         *
         *
         * Please note that the [InputStream] `senderVerificationKey` will not be closed.
         *
         * @param context the app context.
         * @param keychainId the ID of the key manager.
         * @param senderVerificationKey the sender's ECDSA verification key.
         * @return the singleton [RsaEcdsaKeyManager] instance.
         * @throws GeneralSecurityException if the ECDSA verification key could not be initialized.
         * @throws IOException if the ECDSA verification key could not be read.
         */
        @Synchronized
        fun getInstance(
            context: Context, keychainId: String, senderVerificationKey: InputStream
        ): RsaEcdsaKeyManager {
            if (instances.containsKey(keychainId)) {
                val instance: RsaEcdsaKeyManager = instances[keychainId]!!
                senderVerificationKey.let { instance.updateSenderVerifier(it) }
                return instance
            }
            val newInstance = RsaEcdsaKeyManager(keychainId, senderVerificationKey)
            instances[keychainId] = newInstance
            return newInstance
        }
    }

    override fun getDecrypter(keychainuniqueid: String, keyserialnumber: Int, isauthkey: Boolean): HybridDecrypt {
        return rawGetDecrypter(isauthkey)
    }
}

package dev.baseio.security

import com.google.crypto.tink.BinaryKeysetReader
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.HybridDecrypt
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.PublicKeyVerify
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
actual class RsaEcdsaKeyManager constructor(
    chainId: String,
    senderVerificationKey: InputStream
) : KeyManager() {
    private val keychainId = KEY_CHAIN_ID_PREFIX + chainId
    private var keyStore: KeyStore
    private var senderVerifier: PublicKeyVerify

    init {
        val verificationKeyHandle: KeysetHandle = CleartextKeysetHandle
            .read(BinaryKeysetReader.withInputStream(senderVerificationKey))

        senderVerifier = verificationKeyHandle.getPrimitive(PublicKeyVerify::class.java)
        keyStore = Utils.loadKeyStore()
    }

    private fun updateSenderVerifier(senderVerificationKey: InputStream) {
        val verificationKeyHandle: com.google.crypto.tink.KeysetHandle = com.google.crypto.tink.CleartextKeysetHandle
            .read(com.google.crypto.tink.BinaryKeysetReader.withInputStream(senderVerificationKey))
        senderVerifier = com.google.crypto.tink.signature.PublicKeyVerifyFactory.getPrimitive(verificationKeyHandle)
    }

    actual override fun rawGenerateKeyPair(isAuth: Boolean) {
        JVMKeyStoreRsaUtils.generateKeyPair(keychainId, keyStore)
    }

    actual override fun rawGetPublicKey(isAuth: Boolean): ByteArray {
        val publicKeyBytes: ByteArray = JVMKeyStoreRsaUtils.getPublicKey(keyStore, keychainId).encoded
        return kmWrappedRsaEcdsaPublicKey {
            padding = JVMKeyStoreRsaUtils.compatibleRsaPadding.name
            keybytesList.addAll(publicKeyBytes.map {
                kmSKByteArrayElement {
                    byte = it.toInt()
                }
            })
        }.toByteArray()
    }

    override fun rawGetDecrypter(isAuth: Boolean): HybridDecrypt {
        return rawGetDecrypter()
    }

    actual fun decrypt(cipherText: ByteArray, contextInfo: ByteArray?): ByteArray? {
        return rawGetDecrypter().decrypt(cipherText, contextInfo)
    }

    fun rawGetDecrypter(): HybridDecrypt {
        val recipientPrivateKey: PrivateKey = JVMKeyStoreRsaUtils.getPrivateKey(keyStore, keychainId)
        return RsaEcdsaHybridDecrypt.Builder()
            .withRecipientPrivateKey(recipientPrivateKey)
            .withSenderVerifier(senderVerifier)
            .withPadding(JVMKeyStoreRsaUtils.compatibleRsaPadding)
            .build()
    }

    actual override fun rawDeleteKeyPair(isAuth: Boolean) {
        JVMKeyStoreRsaUtils.deleteKeyPair(keyStore, keychainId)
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
            keychainId: String, senderVerificationKey: InputStream
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
}

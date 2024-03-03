package dev.baseio.security

import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore

actual class Capillary actual constructor(chainId: String) {
    private var keyStore: KeyStore = KeyStore.getInstance(AndroidSecurityProvider.KEYSTORE_ANDROID)
    private val keychainId = "rsa_ecdsa_android$chainId"

    actual suspend fun initialize(isTest: Boolean) {
        try {
            keyStore.load(null)
            AndroidKeyStoreRsaUtils.generateKeyPair(keychainId, keyStore)
        } catch (e: IOException) {
            throw GeneralSecurityException("unable to load keystore", e)
        }
    }

    actual suspend fun privateKey(): PrivateKey {
        return AndroidKeyStoreRsaUtils.getPrivateKey(keyStore, keychainId)
    }

    actual suspend fun publicKey(): PublicKey {
        return AndroidKeyStoreRsaUtils.getPublicKey(keyStore, keychainId)
    }

    actual suspend fun encrypt(byteArray: ByteArray, publicKey: PublicKey): EncryptedData {
        return CapillaryEncryption.encrypt(
            byteArray,
            publicKey,
        )
    }

    actual suspend fun decrypt(byteArray: EncryptedData, privateKey: PrivateKey): ByteArray {
        return CapillaryEncryption.decrypt(
            byteArray, privateKey,
        )
    }

    actual suspend fun getPublicKeyFromBytes(publicKeyBytes: ByteArray): PublicKey {
        return AndroidKeyStoreRsaUtils.getPublicKeyFromBytes(publicKeyBytes)
    }
}

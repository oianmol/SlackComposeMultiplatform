package dev.baseio.security

import baseio.security.JSKeyStoreRsaUtils
import baseio.security.NodeForge
import baseio.security.NodeForge.pki.rsa
import kotlinx.coroutines.await

actual class Capillary actual constructor(chainId: String) {
    val keychainId = "rsa_ecdsa_jvm$chainId"

    actual suspend fun initialize(isTest: Boolean) {
        JSKeyStoreRsaUtils.generateKeyPair(keychainId).await()
    }

    actual suspend fun privateKey(): PrivateKey {
        val key = JSKeyStoreRsaUtils.getPrivateKey(keychainId).await()
        return PrivateKey(NodeForge.pki.privateKeyFromPem(key!!))
    }

    actual suspend fun publicKey(): PublicKey {
        val key = JSKeyStoreRsaUtils.getPublicKey(keychainId).await()
        return PublicKey(NodeForge.pki.publicKeyFromPem(key!!))
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
        val publicKey = JSKeyStoreRsaUtils.getPublicKeyFromBytes(publicKeyBytes).await()
        return PublicKey(publicKey as rsa.PublicKey)
    }
}

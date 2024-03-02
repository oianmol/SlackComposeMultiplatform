package dev.baseio.security
import kotlin.js.Promise

external fun require(module: String): dynamic
private val forge = require("node-forge")


actual class Capillary actual constructor(chainId: String) {
    val keychainId = "rsa_ecdsa_jvm$chainId"


    private val forge = require("node-forge")

    actual fun initialize(isTest: Boolean) {
        // Implement key generation using `node-forge` or Web Cryptography API
        // This is a placeholder. You'll need to adapt actual asynchronous JS code into Kotlin
        val keyPair = forge.pki.rsa.generateKeyPair(KEY_SIZE)
        val publicKey = forge.pki.publicKeyToPem(keyPair.publicKey)
        val privateKey = forge.pki.privateKeyToPem(keyPair.privateKey)
        JSKeyStoreRsaUtils.generateKeyPair(keychainId)
    }

    actual fun privateKey(): PrivateKey {
        return JSKeyStoreRsaUtils.getPrivateKey(keychainId)
    }

    actual fun publicKey(): PublicKey {
        return JSKeyStoreRsaUtils.getPublicKey(keychainId)
    }

    actual fun encrypt(byteArray: ByteArray, publicKey: PublicKey): EncryptedData {
        return CapillaryEncryption.encrypt(
            byteArray,
            publicKey,
        )
    }

    actual fun decrypt(byteArray: EncryptedData, privateKey: PrivateKey): ByteArray {
        return CapillaryEncryption.decrypt(
            byteArray, privateKey,
        )
    }

    actual fun getPublicKeyFromBytes(publicKeyBytes: ByteArray): PublicKey {
        return JSKeyStoreRsaUtils.getPublicKeyFromBytes(publicKeyBytes)
    }
}

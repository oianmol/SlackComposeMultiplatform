package dev.baseio.security

actual class Capillary actual constructor(chainId: String) {
    val keychainId = "rsa_ecdsa_jvm$chainId"

    actual suspend fun initialize(isTest: Boolean) {
        JVMKeyStoreRsaUtils.generateKeyPair(keychainId)
    }

    actual suspend fun privateKey(): PrivateKey {
        return JVMKeyStoreRsaUtils.getPrivateKey(keychainId)
    }

    actual suspend fun publicKey(): PublicKey {
        return JVMKeyStoreRsaUtils.getPublicKey(keychainId)
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
        return JVMKeyStoreRsaUtils.getPublicKeyFromBytes(publicKeyBytes)
    }
}

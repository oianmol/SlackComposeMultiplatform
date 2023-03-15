package dev.baseio.security

actual class Capillary actual constructor(chainId: String) {
    val keychainId = "rsa_ecdsa_jvm$chainId"

    actual fun initialize(isTest: Boolean) {
        JVMKeyStoreRsaUtils.generateKeyPair(keychainId)
    }

    actual fun privateKey(): PrivateKey {
        return JVMKeyStoreRsaUtils.getPrivateKey(keychainId)
    }

    actual fun publicKey(): PublicKey {
        return JVMKeyStoreRsaUtils.getPublicKey(keychainId)
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
        return JVMKeyStoreRsaUtils.getPublicKeyFromBytes(publicKeyBytes)
    }
}
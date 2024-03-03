package dev.baseio.security

expect class Capillary(chainId: String) {
    suspend fun initialize(isTest: Boolean)
    suspend fun privateKey(): PrivateKey
    suspend fun publicKey(): PublicKey
    suspend fun encrypt(byteArray: ByteArray, publicKey: PublicKey): EncryptedData
    suspend fun decrypt(byteArray: EncryptedData, privateKey: PrivateKey): ByteArray
    suspend fun getPublicKeyFromBytes(publicKeyBytes: ByteArray): PublicKey
}

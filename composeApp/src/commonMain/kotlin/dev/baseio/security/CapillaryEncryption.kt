package dev.baseio.security

const val TRANSFORMATION_ASYMMETRIC = "RSA/ECB/PKCS1Padding"
const val KEY_SIZE: Int = 2048

typealias EncryptedData = Pair<String, String>

expect object CapillaryEncryption {

    suspend fun encrypt(
        plaintext: ByteArray,
        publicKey: PublicKey,
    ): EncryptedData

    suspend fun decrypt(
        encryptedData: EncryptedData,
        privateKey: PrivateKey,
    ): ByteArray
}

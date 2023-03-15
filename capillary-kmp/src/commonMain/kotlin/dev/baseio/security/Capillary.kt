package dev.baseio.security

expect class Capillary(chainId: String) {
  fun initialize(isTest: Boolean)
  fun privateKey(): PrivateKey
  fun publicKey(): PublicKey
  fun encrypt(byteArray: ByteArray, publicKey: PublicKey): EncryptedData
  fun decrypt(byteArray: EncryptedData, privateKey: PrivateKey): ByteArray
  fun getPublicKeyFromBytes(publicKeyBytes: ByteArray): PublicKey
}
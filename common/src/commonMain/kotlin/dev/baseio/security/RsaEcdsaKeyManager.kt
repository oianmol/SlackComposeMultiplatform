package dev.baseio.security

expect class RsaEcdsaKeyManager {
    fun rawDeleteKeyPair(isAuth: Boolean)
    fun rawGetPublicKey(isAuth: Boolean): ByteArray
    fun rawGenerateKeyPair(isAuth: Boolean)
    fun decrypt(cipherText: ByteArray, contextInfo: ByteArray?): ByteArray?
}
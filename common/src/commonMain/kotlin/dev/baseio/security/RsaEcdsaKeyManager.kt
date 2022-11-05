package dev.baseio.security

expect class RsaEcdsaKeyManager : KeyManager {
    fun rawDeleteKeyPair(isAuth: Boolean)
    fun rawGetPublicKey(isAuth: Boolean): ByteArray
    fun rawGenerateKeyPair(isAuth: Boolean)
}
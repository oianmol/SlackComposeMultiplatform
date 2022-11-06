package dev.baseio.security

expect class WebPushKeyManager {
    fun rawGenerateKeyPair(isAuth: Boolean)
    fun rawGetPublicKey(isAuth: Boolean): ByteArray
    fun rawDeleteKeyPair(isAuth: Boolean)

    fun decrypt(cipherText: ByteArray, contextInfo: ByteArray?): ByteArray?
}

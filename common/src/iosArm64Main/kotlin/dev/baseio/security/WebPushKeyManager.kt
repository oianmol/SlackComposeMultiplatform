package dev.baseio.security

actual class WebPushKeyManager : KeyManager() {
    actual fun rawGenerateKeyPair(isAuth: Boolean) {
    }

    actual fun rawGetPublicKey(isAuth: Boolean): ByteArray {
        TODO("Not yet implemented")
    }

    actual fun rawDeleteKeyPair(isAuth: Boolean) {
    }

    actual fun decrypt(cipherText: ByteArray, contextInfo: ByteArray?): ByteArray? {
        TODO("Not yet implemented")
    }
}
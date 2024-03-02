package dev.baseio.security

actual class PrivateKey(var privateKey: PrivateKey) {
    actual var encoded: ByteArray = privateKey.encoded
}

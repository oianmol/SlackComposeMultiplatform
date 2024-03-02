package dev.baseio.security

actual class PrivateKey(var encodedBytes: ByteArray) {
    actual var encoded: ByteArray = encodedBytes
}

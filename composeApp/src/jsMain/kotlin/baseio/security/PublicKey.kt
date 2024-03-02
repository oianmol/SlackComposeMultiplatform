package dev.baseio.security

actual class PublicKey(var publicKey: PublicKey) {
    actual var encoded: ByteArray = publicKey.encoded
}

package dev.baseio.security

actual class PublicKey(encodedBytes:ByteArray) {
  actual var encoded: ByteArray = encodedBytes
}


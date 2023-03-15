package dev.baseio.security

import java.security.PrivateKey

actual class PrivateKey(var privateKey: PrivateKey) {
  actual var encoded: ByteArray = privateKey.encoded
}
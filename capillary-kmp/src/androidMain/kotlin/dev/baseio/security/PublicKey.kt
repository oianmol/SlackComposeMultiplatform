package dev.baseio.security

import java.security.PublicKey

actual class PublicKey(var publicKey: PublicKey) {
  actual var encoded: ByteArray = publicKey.encoded
}
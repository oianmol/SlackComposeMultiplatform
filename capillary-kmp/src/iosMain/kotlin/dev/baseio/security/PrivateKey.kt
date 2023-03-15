package dev.baseio.security

import platform.Security.*

actual class PrivateKey(var encodedBytes: ByteArray){
  actual var encoded: ByteArray = encodedBytes
}


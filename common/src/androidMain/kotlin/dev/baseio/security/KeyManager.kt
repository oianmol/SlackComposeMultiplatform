package dev.baseio.security

import com.google.crypto.tink.HybridDecrypt

actual abstract class KeyManager{
    abstract fun getDecrypter(keychainuniqueid: String, keyserialnumber: Int, isauthkey: Boolean): HybridDecrypt
}
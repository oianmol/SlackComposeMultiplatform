package dev.baseio.security

import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.signature.SignatureConfig

actual object Capillary {
    actual fun initialize() {
        com.google.crypto.tink.Config.register(SignatureConfig.LATEST);
        AeadConfig.register()
    }
}
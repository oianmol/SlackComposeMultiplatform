package dev.baseio.security

import cocoapods.Tink.TINKAeadConfig
import cocoapods.Tink.TINKSignatureConfig

actual object Capillary {
    actual fun initialize() {
        cocoapods.Tink.TINKConfig.registerConfig(TINKSignatureConfig.new()!!, null)
        cocoapods.Tink.TINKConfig.registerConfig(TINKAeadConfig.new()!!, null)
    }
}
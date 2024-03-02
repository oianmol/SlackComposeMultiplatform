package dev.baseio.security

import cocoapods.capillaryslack.CapillaryIOS
import dev.baseio.extensions.toByteArrayFromNSData
import dev.baseio.extensions.toData

actual class Capillary actual constructor(chainId: String) {
    private val keychainId = "rsa_ios$chainId"

    actual fun initialize(isTest: Boolean) {
        CapillaryIOS.initNowWithChainId(keychainId, isTest)
    }

    actual fun publicKey(): PublicKey {
        return PublicKey(
            encodedBytes = CapillaryIOS.publicKeyWithChainId(keychainId)!!.toByteArrayFromNSData()
        )
    }

    actual fun privateKey(): PrivateKey {
        return PrivateKey(
            encodedBytes = CapillaryIOS.privateKeyWithChainId(keychainId)!!.toByteArrayFromNSData()
        )
    }

    actual fun encrypt(byteArray: ByteArray, publicKey: PublicKey): EncryptedData {
        return CapillaryEncryption.encrypt(
            byteArray,
            publicKey
        )
    }

    actual fun decrypt(byteArray: EncryptedData, privateKey: PrivateKey): ByteArray {
        return CapillaryEncryption.decrypt(
            byteArray, privateKey
        )
    }

    actual fun getPublicKeyFromBytes(publicKeyBytes: ByteArray): PublicKey {
        return PublicKey(
            CapillaryIOS.publicKeyFromBytesWithData(publicKeyBytes.toData())!!
                .toByteArrayFromNSData()
        )
    }
}

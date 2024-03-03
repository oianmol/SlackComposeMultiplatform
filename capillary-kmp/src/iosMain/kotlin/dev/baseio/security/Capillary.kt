package dev.baseio.security

import cocoapods.capillaryslack.CapillaryIOS
import dev.baseio.extensions.toByteArrayFromNSData
import dev.baseio.extensions.toData
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class Capillary actual constructor(chainId: String) {
    private val keychainId = "rsa_ios$chainId"

    actual suspend fun initialize(isTest: Boolean) {
        CapillaryIOS.initNowWithChainId(keychainId, isTest)
    }

    actual suspend fun publicKey(): PublicKey {
        return PublicKey(
            encodedBytes = CapillaryIOS.publicKeyWithChainId(keychainId)!!.toByteArrayFromNSData()
        )
    }

    actual suspend fun privateKey(): PrivateKey {
        return PrivateKey(
            encodedBytes = CapillaryIOS.privateKeyWithChainId(keychainId)!!.toByteArrayFromNSData()
        )
    }

    actual suspend fun encrypt(byteArray: ByteArray, publicKey: PublicKey): EncryptedData {
        return CapillaryEncryption.encrypt(
            byteArray,
            publicKey
        )
    }

    actual suspend fun decrypt(byteArray: EncryptedData, privateKey: PrivateKey): ByteArray {
        return CapillaryEncryption.decrypt(
            byteArray, privateKey
        )
    }

    actual suspend fun getPublicKeyFromBytes(publicKeyBytes: ByteArray): PublicKey {
        return PublicKey(
            CapillaryIOS.publicKeyFromBytesWithData(publicKeyBytes.toData())!!
                .toByteArrayFromNSData()
        )
    }
}

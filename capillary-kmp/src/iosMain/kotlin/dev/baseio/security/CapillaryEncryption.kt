@file:OptIn(BetaInteropApi::class)

package dev.baseio.security

import cocoapods.capillaryslack.CapillaryIOS
import dev.baseio.extensions.toByteArrayFromNSData
import dev.baseio.extensions.toData
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import platform.Foundation.NSData

@OptIn(ExperimentalForeignApi::class)
actual object CapillaryEncryption {
    actual suspend fun encrypt(
        plaintext: ByteArray,
        publicKey: PublicKey
    ): EncryptedData {
        autoreleasepool {
            val encryptedResponse =
                CapillaryIOS.encryptWithData(plaintext.toData(), publicKey.encoded.toData())
            return EncryptedData(
                encryptedResponse.firstItem() ?: "",
                encryptedResponse.secondItem() ?: ""
            )
        }
    }

    actual suspend fun decrypt(
        encryptedData: EncryptedData,
        privateKey: PrivateKey
    ): ByteArray {
        autoreleasepool {
            NSData
            return CapillaryIOS.decryptWithSymmetricKeyCiphertext(
                encryptedData.first, encryptedData.second,
                privateKey.encodedBytes.toData()
            )!!.toByteArrayFromNSData()
        }
    }
}

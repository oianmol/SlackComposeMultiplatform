package dev.baseio.security

import cocoapods.capillaryslack.CapillaryIOS
import dev.baseio.extensions.toByteArrayFromNSData
import dev.baseio.extensions.toData
import kotlinx.cinterop.autoreleasepool
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.encodeUtf8
import okio.ByteString.Companion.toByteString
import platform.Foundation.*

actual object CapillaryEncryption {
  actual fun encrypt(
    plaintext: ByteArray,
    publicKey: PublicKey,
  ): EncryptedData {
    autoreleasepool {
      val encryptedResponse = CapillaryIOS.encryptWithData(plaintext.toData(), publicKey.encoded.toData())
      return EncryptedData(
        encryptedResponse.firstItem() ?: "",
        encryptedResponse.secondItem() ?: ""
      )
    }
  }

  actual fun decrypt(
    encryptedData: EncryptedData,
    privateKey: PrivateKey,
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
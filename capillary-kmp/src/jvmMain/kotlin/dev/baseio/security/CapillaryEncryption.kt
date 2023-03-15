@file:JvmName("CapillaryEncryptionJAva")

package dev.baseio.security

import java.util.Base64
import javax.crypto.Cipher

actual object CapillaryEncryption {
  actual fun encrypt(
    plaintext: ByteArray,
    publicKey: PublicKey,
  ): EncryptedData {
    val cipher: Cipher = Cipher.getInstance(TRANSFORMATION_ASYMMETRIC)
    cipher.init(Cipher.ENCRYPT_MODE, publicKey.publicKey)
    val secretKey = CryptoChaCha20.createSymmetricKey()
    val payloadCiphertext = CryptoChaCha20.encrypt(plaintext, secretKey)
    val symmetricKeyCiphertext = cipher.doFinal(secretKey.encoded)
    return Pair(symmetricKeyCiphertext.base64(), payloadCiphertext.base64())
  }

  actual fun decrypt(
    encryptedData: EncryptedData,
    privateKey: PrivateKey,
  ): ByteArray {
    val rsaCipher = Cipher.getInstance(TRANSFORMATION_ASYMMETRIC)
    rsaCipher.init(Cipher.DECRYPT_MODE, privateKey.privateKey)
    // Retrieve symmetric key.
    val symmetricKeyBytes = rsaCipher.doFinal(encryptedData.first.frombase64())
    return CryptoChaCha20.decrypt(
      encryptedData.second.frombase64()!!, CryptoChaCha20
        .secretFrom(symmetricKeyBytes)
    )
  }
}

private fun String.frombase64(): ByteArray? {
  return Base64.getDecoder().decode(this)
}

private fun ByteArray.base64(): String {
  return Base64.getEncoder().encodeToString(this)
}

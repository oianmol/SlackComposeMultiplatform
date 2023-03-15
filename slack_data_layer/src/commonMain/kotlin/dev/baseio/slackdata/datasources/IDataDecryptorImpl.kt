package dev.baseio.slackdata.datasources

import dev.baseio.security.CapillaryEncryption
import dev.baseio.security.EncryptedData
import dev.baseio.security.toPrivateKey
import dev.baseio.slackdomain.datasources.IDataDecryptor

class IDataDecryptorImpl : IDataDecryptor {
  override fun decrypt(byteArray: EncryptedData, privateKeyBytes: ByteArray): ByteArray {
    return CapillaryEncryption.decrypt(
      byteArray, privateKeyBytes.toPrivateKey()
    )
  }

}
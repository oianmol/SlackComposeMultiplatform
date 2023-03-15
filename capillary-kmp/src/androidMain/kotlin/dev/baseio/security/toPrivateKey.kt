package dev.baseio.security

import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

actual fun ByteArray.toPrivateKey(): PrivateKey {
  val spec = PKCS8EncodedKeySpec(this)
  val kf = KeyFactory.getInstance("RSA")
  return PrivateKey(kf.generatePrivate(spec))
}

actual fun ByteArray.toPublicKey(): PublicKey {
  return PublicKey(KeyFactory.getInstance("RSA").generatePublic(
    X509EncodedKeySpec(this)
  ))
}
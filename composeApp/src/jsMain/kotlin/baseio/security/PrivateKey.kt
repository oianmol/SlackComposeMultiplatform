package dev.baseio.security

import baseio.security.NodeForge
import baseio.security.NodeForge.pki.rsa
import io.ktor.utils.io.core.toByteArray

actual class PrivateKey(var privateKey: rsa.PrivateKey) {
    actual var encoded: ByteArray = getEncodedPrivateKey(privateKey)
}

fun getEncodedPrivateKey(privateKey: rsa.PrivateKey): ByteArray {
    val asn1 = NodeForge.pki.privateKeyToAsn1(privateKey)
    val der = NodeForge.asn1.toDer(asn1).getBytes()
    return der.toByteArray()
}
package dev.baseio.security

import baseio.security.NodeForge
import baseio.security.NodeForge.pki.rsa.PublicKey
import io.ktor.utils.io.core.toByteArray

actual class PublicKey(var publicKey: NodeForge.pki.rsa.PublicKey) {
    actual var encoded: ByteArray = getEncodedPublicKey(publicKey)
}

fun getEncodedPublicKey(publicKey: PublicKey): ByteArray {
    // Assuming publicKey is your node-forge public key object
    val asn1PublicKey = NodeForge.pki.publicKeyToAsn1(publicKey); // Convert to ASN.1
    val derPublicKey = NodeForge.asn1.toDer(asn1PublicKey).getBytes(); // Serialize ASN.1 to DER

// If you need the result in a format like Base64, which is common:
    val base64EncodedPublicKey = NodeForge.util.encode64(derPublicKey);
    return base64EncodedPublicKey.toByteArray()
}
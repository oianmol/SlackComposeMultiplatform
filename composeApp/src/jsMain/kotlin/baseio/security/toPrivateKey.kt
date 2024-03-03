package dev.baseio.security

import baseio.security.NodeForge
import kotlinx.coroutines.await
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.Promise

@OptIn(ExperimentalEncodingApi::class)
actual suspend fun ByteArray.toPrivateKey() = Promise { resolve, reject ->
    try {
        // Convert ByteArray to Base64 as Forge expects a PEM string for private keys
        val base64Encoded = Base64.encode(this)
        // Begin and end markers are added to denote a PEM-formatted key
        val pemKey = "-----BEGIN PRIVATE KEY-----\n$base64Encoded\n-----END PRIVATE KEY-----"
        val privateKey = NodeForge.pki.privateKeyFromPem(pemKey)
        resolve(PrivateKey(privateKey)) // Wrap the Forge private key in your Kotlin class
    } catch (e: Exception) {
        reject(e)
    }
}.await()

@OptIn(ExperimentalEncodingApi::class)
actual suspend fun ByteArray.toPublicKey() = Promise { resolve, reject ->
    try {
        // Convert ByteArray to Base64 as Forge expects a PEM string for public keys
        val base64Encoded = Base64.encode(this)
        // Begin and end markers are added to denote a PEM-formatted key
        val pemKey = "-----BEGIN PUBLIC KEY-----\n$base64Encoded\n-----END PUBLIC KEY-----"
        val publicKey = NodeForge.pki.publicKeyFromPem(pemKey)
        resolve(PublicKey(publicKey)) // Wrap the Forge public key in your Kotlin class
    } catch (e: Exception) {
        reject(e)
    }
}.await()

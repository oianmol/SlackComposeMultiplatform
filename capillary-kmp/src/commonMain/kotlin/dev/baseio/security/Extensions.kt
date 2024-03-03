package dev.baseio.security

expect suspend fun ByteArray.toPrivateKey(): PrivateKey
expect suspend fun ByteArray.toPublicKey(): PublicKey

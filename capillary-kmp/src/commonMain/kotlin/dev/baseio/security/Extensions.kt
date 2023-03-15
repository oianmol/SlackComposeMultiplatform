package dev.baseio.security

expect fun ByteArray.toPrivateKey(): PrivateKey
expect fun ByteArray.toPublicKey(): PublicKey
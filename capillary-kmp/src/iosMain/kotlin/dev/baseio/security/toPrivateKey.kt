package dev.baseio.security

import dev.baseio.extensions.toByteArrayFromNSData
import dev.baseio.extensions.toData
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual suspend fun ByteArray.toPrivateKey(): PrivateKey {
    return PrivateKey(
        cocoapods.capillaryslack.CapillaryIOS.privateKeyFromBytesWithData(this.toData())!!.toByteArrayFromNSData()
    )
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun ByteArray.toPublicKey(): PublicKey {
    return PublicKey(
        cocoapods.capillaryslack.CapillaryIOS.publicKeyFromBytesWithData(this.toData())!!.toByteArrayFromNSData()
    )
}

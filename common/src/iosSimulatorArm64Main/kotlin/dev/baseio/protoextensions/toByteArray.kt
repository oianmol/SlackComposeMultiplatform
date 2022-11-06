package dev.baseio.protoextensions

import dev.baseio.slackdata.securepush.KMHybridRsaCiphertext
import dev.baseio.slackdata.securepush.KMSecureNotification
import dev.baseio.slackdata.securepush.KMSlackCiphertext
import dev.baseio.slackdata.securepush.KMSlackPublicKey
import dev.baseio.slackdata.securepush.KMWrappedRsaEcdsaPublicKey
import dev.baseio.slackdata.securepush.KMWrappedWebPushPrivateKey
import dev.baseio.slackdata.securepush.KMWrappedWebPushPublicKey

actual fun KMSlackPublicKey.toByteArray(): ByteArray {
    TODO("Not yet implemented")
}

actual fun ByteArray.toSecureNotification(): KMSecureNotification {
    TODO("Not yet implemented")
}

actual fun ByteArray.toSlackCipherText(): KMSlackCiphertext {
    TODO("Not yet implemented")
}

actual fun ByteArray.toKMWrappedWebPushPrivateKey(): KMWrappedWebPushPrivateKey {
    TODO("Not yet implemented")
}

actual fun ByteArray.toKMHybridRsaCiphertext(): KMHybridRsaCiphertext {
    TODO("Not yet implemented")
}

actual fun KMWrappedWebPushPublicKey.toByteArray(): ByteArray {
    TODO("Not yet implemented")
}

actual fun KMWrappedWebPushPrivateKey.toByteArray(): ByteArray {
    TODO("Not yet implemented")
}

actual fun KMHybridRsaCiphertext.toByteArray(): ByteArray {
    TODO("Not yet implemented")
}

actual fun KMWrappedRsaEcdsaPublicKey.toByteArray(): ByteArray {
    TODO("Not yet implemented")
}

actual fun KMSecureNotification.toByteArray(): ByteArray {
    TODO("Not yet implemented")
}
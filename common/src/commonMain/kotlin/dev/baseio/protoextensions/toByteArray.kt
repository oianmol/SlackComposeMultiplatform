package dev.baseio.protoextensions

import dev.baseio.slackdata.securepush.KMHybridRsaCiphertext
import dev.baseio.slackdata.securepush.KMWrappedRsaEcdsaPublicKey
import dev.baseio.slackdata.securepush.KMWrappedWebPushPrivateKey
import dev.baseio.slackdata.securepush.KMWrappedWebPushPublicKey

expect fun KMWrappedWebPushPublicKey.toByteArray(): ByteArray
expect fun KMWrappedWebPushPrivateKey.toByteArray(): ByteArray
expect fun KMHybridRsaCiphertext.toByteArray(): ByteArray
expect fun KMWrappedRsaEcdsaPublicKey.toByteArray(): ByteArray


expect fun ByteArray.toKMWrappedWebPushPrivateKey(): KMWrappedWebPushPrivateKey
package dev.baseio.slackdata

import dev.baseio.slackdata.protos.KMSKEncryptedMessage
import dev.baseio.slackdata.protos.kmSKEncryptedMessage


fun Pair<ByteArray, ByteArray>.toSKEncryptedMessage(): KMSKEncryptedMessage {
    return kmSKEncryptedMessage {
        this.first = this@toSKEncryptedMessage.first.decodeToString()
        this.second = this@toSKEncryptedMessage.second.decodeToString()
    }
}

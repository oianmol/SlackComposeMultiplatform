package dev.baseio.slackserver.data.models

data class SKUserPublicKey(
    val keyBytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SKUserPublicKey

        if (!keyBytes.contentEquals(other.keyBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return keyBytes.contentHashCode()
    }
}
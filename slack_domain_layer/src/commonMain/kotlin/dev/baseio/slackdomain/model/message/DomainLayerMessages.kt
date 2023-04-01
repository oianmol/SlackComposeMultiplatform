package dev.baseio.slackdomain.model.message

import dev.baseio.slackdomain.model.channel.DomainLayerChannels

interface DomainLayerMessages {
    data class SKMessage(
        val uuid: String,
        val workspaceId: String,
        val channelId: String,
        val messageFirst: String = "",
        val messageSecond: String = "",
        val sender: String,
        val createdDate: Long,
        val modifiedDate: Long,
        var isDeleted: Boolean = false,
        var isSynced: Boolean = false,
        var decodedMessage: String = "Waiting for message."
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as SKMessage

            if (uuid != other.uuid) return false
            if (workspaceId != other.workspaceId) return false
            if (channelId != other.channelId) return false
            if (!messageFirst.contentEquals(other.messageFirst)) return false
            if (!messageSecond.contentEquals(other.messageSecond)) return false
            if (sender != other.sender) return false
            if (createdDate != other.createdDate) return false
            if (modifiedDate != other.modifiedDate) return false
            if (isDeleted != other.isDeleted) return false
            if (isSynced != other.isSynced) return false
            if (decodedMessage != other.decodedMessage) return false

            return true
        }

        override fun hashCode(): Int {
            var result = uuid.hashCode()
            result = 31 * result + workspaceId.hashCode()
            result = 31 * result + channelId.hashCode()
            result = 31 * result + messageFirst.hashCode()
            result = 31 * result + messageSecond.hashCode()
            result = 31 * result + sender.hashCode()
            result = 31 * result + createdDate.hashCode()
            result = 31 * result + modifiedDate.hashCode()
            result = 31 * result + isDeleted.hashCode()
            result = 31 * result + isSynced.hashCode()
            result = 31 * result + decodedMessage.hashCode()
            return result
        }
    }

    data class SKLastMessage(
        val channel: DomainLayerChannels.SKChannel,
        val message: SKMessage
    )
}

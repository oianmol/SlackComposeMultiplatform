package dev.baseio.slackdomain.model.channel

import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.datetime.Clock

interface DomainLayerChannels {

    sealed class SKChannel(
        var workspaceId: String,
        var channelId: String,
        var pictureUrl: String? = null,
        var channelName: String? = null,
        val publicKey: DomainLayerUsers.SKSlackKey
    ) {
        data class SkDMChannel(
            var uuid: String,
            val workId: String,
            var senderId: String,
            var receiverId: String,
            val createdDate: Long = Clock.System.now().toEpochMilliseconds(),
            val modifiedDate: Long = Clock.System.now().toEpochMilliseconds(),
            val deleted: Boolean,
            val channelPublicKey: DomainLayerUsers.SKSlackKey
        ) : SKChannel(workId, uuid, publicKey = channelPublicKey)

        data class SkGroupChannel(
            var uuid: String,
            val workId: String,
            var name: String,
            val createdDate: Long = Clock.System.now().toEpochMilliseconds(),
            val modifiedDate: Long = Clock.System.now().toEpochMilliseconds(),
            var avatarUrl: String?,
            val deleted: Boolean,
            val channelPublicKey: DomainLayerUsers.SKSlackKey
        ) : SKChannel(
            workId,
            uuid,
            channelName = name,
            pictureUrl = avatarUrl,
            publicKey = channelPublicKey
        )
    }

    data class SkChannelMember(
        val uuid: String,
        val workspaceId: String,
        val channelId: String,
        val memberId: String,
        val channelEncryptedPrivateKey: DomainLayerUsers.SKEncryptedMessage
    )
}

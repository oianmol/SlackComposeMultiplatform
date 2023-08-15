package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.security.CapillaryInstances
import dev.baseio.security.EncryptedData
import dev.baseio.slackdata.datasources.local.channels.loggedInUser
import dev.baseio.slackdata.protos.KMSKEncryptedMessage
import dev.baseio.slackdomain.datasources.IDataEncrypter
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkSourceChannel
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class SKNetworkSourceChannelImpl(
    private val grpcCalls: IGrpcCalls,
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers,
    private val skLocalDataSourceChannelMembers: SKLocalDataSourceChannelMembers,
    private val iDataEncrypter: IDataEncrypter,
    private val skLocalKeyValueSource: SKLocalKeyValueSource
) : SKNetworkSourceChannel {

    private suspend fun inviteUserInternal(
        userName: String,
        channelId: String,
        channelEncryptedPrivateKey: DomainLayerUsers.SKEncryptedMessage
    ): List<DomainLayerChannels.SkChannelMember> {
        return grpcCalls.inviteUserToChannel(
            userName,
            channelId,
            channelEncryptedPrivateKey
        ).membersList.map { kmskChannelMember ->
            DomainLayerChannels.SkChannelMember(
                kmskChannelMember.uuid,
                kmskChannelMember.workspaceId,
                kmskChannelMember.channelId,
                kmskChannelMember.memberId,
                channelEncryptedPrivateKey = kmskChannelMember.channelPrivateKey.toDomainSKEncryptedMessage()
            )
        }.also {
            skLocalDataSourceChannelMembers.save(it)
        }
    }

    override suspend fun inviteUserToChannelFromOtherDeviceOrUser(
        channel: DomainLayerChannels.SKChannel,
        userName: String
    ): List<DomainLayerChannels.SkChannelMember> {
        val user = skLocalKeyValueSource.loggedInUser(channel.workspaceId)
        val channelEncryptedPrivateKeysForLoggedInUser =
            skLocalDataSourceChannelMembers.getChannelPrivateKeyForMe(
                channel.workspaceId,
                channel.channelId,
                user?.uuid ?: "" // TODO check if passing empty or throwing exception is better here
            ).map { it.channelEncryptedPrivateKey }
        val capillary = CapillaryInstances.getInstance(user?.email!!)

        val decryptedChannelPrivateKeyForLoggedInUser =
            channelEncryptedPrivateKeysForLoggedInUser.firstNotNullOfOrNull { channelEncryptedPrivateKeyForLoggedInUser ->
                kotlin.runCatching {
                    capillary.decrypt(
                        EncryptedData(
                            channelEncryptedPrivateKeyForLoggedInUser.first,
                            channelEncryptedPrivateKeyForLoggedInUser.second
                        ),
                        capillary.privateKey()
                    )
                }.getOrNull()
            }

        val channelPrivateKeyEncryptedForInvitedUser =
            decryptedChannelPrivateKeyForLoggedInUser?.let {
                iDataEncrypter.encrypt(
                    it,
                    skLocalDataSourceUsers.getUserByUserName(
                        channel.workspaceId,
                        userName
                    )!!.publicKey!!.keyBytes
                )
            } // TODO fix this ask the backend if not available in local cache!
        return channelPrivateKeyEncryptedForInvitedUser?.let {
            inviteUserInternal(
                userName, channel.channelId,
                it
            )
        } ?: run {
            // TODO check, why do we have empty list here ?
            emptyList()
        }
    }
}

fun KMSKEncryptedMessage.toDomainSKEncryptedMessage(): DomainLayerUsers.SKEncryptedMessage {
    return DomainLayerUsers.SKEncryptedMessage(this.first, this.second)
}

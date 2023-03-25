package dev.baseio.slackserver.services

import dev.baseio.security.CapillaryInstances
import dev.baseio.security.EncryptedData
import dev.baseio.security.JVMKeyStoreRsaUtils
import dev.baseio.slackdata.common.sKByteArrayElement
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.communications.NotificationType
import dev.baseio.slackserver.communications.PNSender
import dev.baseio.slackserver.data.models.SKUserPublicKey
import dev.baseio.slackserver.data.sources.ChannelsDataSource
import dev.baseio.slackserver.data.models.SkChannel
import dev.baseio.slackserver.data.models.SkChannelMember
import dev.baseio.slackserver.data.sources.ChannelMemberDataSource
import dev.baseio.slackserver.data.sources.UsersDataSource
import dev.baseio.slackserver.services.interceptors.AUTH_CONTEXT_KEY
import dev.baseio.slackserver.services.interceptors.AuthData
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.coroutines.CoroutineContext

class ChannelService(
    coroutineContext: CoroutineContext = Dispatchers.IO,
    private val channelsDataSource: ChannelsDataSource,
    private val channelMemberDataSource: ChannelMemberDataSource,
    private val usersDataSource: UsersDataSource,
    private val channelPNSender: PNSender<SkChannel>,
    private val channelMemberPNSender: PNSender<SkChannelMember>,
) :
    ChannelsServiceGrpcKt.ChannelsServiceCoroutineImplBase(coroutineContext) {

    override suspend fun inviteUserToChannel(request: SKInviteUserChannel): SKChannelMembers {
        val userData = AUTH_CONTEXT_KEY.get()
        return inviteUserWithAuthData(request, userData)
    }


    override suspend fun joinChannel(request: SKChannelMember): SKChannelMember {
        channelMemberDataSource.addMembers(listOf(request.toDBMember()))
        return request
    }

    override suspend fun channelMembers(request: SKWorkspaceChannelRequest): SKChannelMembers {
        return channelMemberDataSource.getMembers(request.workspaceId, request.channelId).run {
            SKChannelMembers.newBuilder()
                .addAllMembers(this.map { it.toGRPC() })
                .build()
        }
    }

    override suspend fun getAllChannels(request: SKChannelRequest): SKChannels {
        val userData = AUTH_CONTEXT_KEY.get()
        return channelsDataSource.getAllChannels(request.workspaceId, userData.userId).run {
            SKChannels.newBuilder()
                .addAllChannels(this.map { it.toGRPC() })
                .build()
        }
    }

    override suspend fun getAllDMChannels(request: SKChannelRequest): SKDMChannels {
        val userData = AUTH_CONTEXT_KEY.get()
        return channelsDataSource.getAllDMChannels(request.workspaceId, userData.userId).run {
            SKDMChannels.newBuilder()
                .addAllChannels(this.map { it.toGRPC() })
                .build()
        }
    }

    override suspend fun savePublicChannel(request: SKChannel): SKChannel {
        val authData = AUTH_CONTEXT_KEY.get()
        val previousChannelExists = channelsDataSource.checkIfGroupExisits(request.workspaceId, request.name)
        if (previousChannelExists) {
            val groupChannel =
                channelsDataSource.getChannelByName(request.name, request.workspaceId) as SkChannel.SkGroupChannel
            return groupChannel.toGRPC()
        }

        val skGroupChannel = request.toDBChannel()

        val userPublicKey = skUserPublicKey(authData, request)
        val skChannelSlackKeyPair = with(CapillaryInstances.getInstance(skGroupChannel.uuid)) {
            val publicKeyChannel = publicKey().encoded //create the channel public key!
            // save the channel with the public key of channel
            val saved = channelsDataSource.savePublicChannel(
                skGroupChannel.copy(channelPublicKey = SKUserPublicKey(publicKeyChannel)),
                adminId = authData.userId
            )?.toGRPC()

            val channelPrivateKey =
                encrypt(privateKey().encoded, userPublicKey.toPublicKey()).toSlackKey()
            JVMKeyStoreRsaUtils.deleteKeyPair(keychainId)
            Pair(saved, channelPrivateKey)
        }

        // invite the user to his channel
        inviteUserWithAuthData(sKInviteUserChannel {
            this.channelId = skGroupChannel.uuid
            this.userId = authData.userId
            this.channelPrivateKey = skChannelSlackKeyPair.second
        }, authData)

        sendPushNotifyChannelCreated(skGroupChannel, authData)

        return skChannelSlackKeyPair.first ?: throw StatusException(Status.FAILED_PRECONDITION)

    }

    private fun sendPushNotifyChannelCreated(
        channel: SkChannel.SkGroupChannel,
        authData: AuthData
    ) {
        channelPNSender.sendPushNotifications(
            channel,
            authData.userId,
            NotificationType.CHANNEL_CREATED
        )
    }

    private suspend fun skUserPublicKey(
        authData: AuthData,
        request: SKChannel
    ) = usersDataSource.getUser(
        authData.userId,
        request.workspaceId
    )!!.publicKey

    override suspend fun saveDMChannel(request: SKDMChannel): SKDMChannel {
        val authData = AUTH_CONTEXT_KEY.get()
        val previousChannel = channelsDataSource.checkIfDMChannelExists(request.senderId, request.receiverId)
        previousChannel?.let {
            return it.toGRPC()
        } ?: kotlin.run {
            val keyManager = CapillaryInstances.getInstance(request.uuid)
            val publicKeyChannel = keyManager.publicKey().encoded
            val channel = dbChannel(request, publicKeyChannel)
            val savedChannel = channelsDataSource.saveDMChannel(channel)?.toGRPC()!!
            inviteUserWithAuthData(sKInviteUserChannel {
                this.channelId = savedChannel.uuid
                this.userId = request.senderId

                val userPublicKey = usersDataSource.getUser(
                    request.senderId,
                    request.workspaceId
                )!!.publicKey
                this.channelPrivateKey =
                    keyManager.encrypt(keyManager.privateKey().encoded, userPublicKey.toPublicKey())
                        .toSlackKey()
            }, authData)
            inviteUserWithAuthData(sKInviteUserChannel {
                this.channelId = savedChannel.uuid
                this.userId = request.receiverId

                val userPublicKey = usersDataSource.getUser(
                    request.receiverId,
                    request.workspaceId
                )!!.publicKey
                this.channelPrivateKey =
                    keyManager.encrypt(keyManager.privateKey().encoded, userPublicKey.toPublicKey())
                        .toSlackKey()

            }, authData)
            channelPNSender.sendPushNotifications(
                channel,
                authData.userId,
                NotificationType.DM_CHANNEL_CREATED
            )
            JVMKeyStoreRsaUtils.deleteKeyPair(keyManager.keychainId)
            return savedChannel
        }
    }

    private suspend fun inviteUserWithAuthData(
        request: SKInviteUserChannel,
        userData: AuthData
    ): SKChannelMembers {
        val user = usersDataSource.getUserWithUsername(userName = request.userId, userData.workspaceId)
            ?: usersDataSource.getUserWithUserId(userId = request.userId, userData.workspaceId)
        val channel =
            channelsDataSource.getChannelById(request.channelId, userData.workspaceId)
                ?: channelsDataSource.getChannelByName(
                    request.channelId,
                    userData.workspaceId
                )
        user?.let { safeUser ->
            channel?.let { channel ->
                joinChannel(sKChannelMember {
                    this.channelId = channel.channelId
                    this.memberId = safeUser.uuid
                    this.workspaceId = userData.workspaceId
                    this.channelPrivateKey = request.channelPrivateKey
                }.also {
                    channelMemberPNSender.sendPushNotifications(
                        it.toDBMember(),
                        userData.userId,
                        NotificationType.ADDED_CHANNEL
                    )
                })

                return channelMembers(sKWorkspaceChannelRequest {
                    this.channelId = channel.channelId
                    this.workspaceId = userData.workspaceId
                })
            } ?: run {
                throw StatusException(Status.NOT_FOUND)
            }

        } ?: run {
            throw StatusException(Status.NOT_FOUND)
        }
    }

    private fun dbChannel(
        request: SKDMChannel,
        publicKeyChannel: ByteArray
    ): SkChannel.SkDMChannel {
        val channel = request.copy {
            uuid = request.uuid.takeIf { it.isNotEmpty() } ?: UUID.randomUUID().toString()
            createdDate = System.currentTimeMillis()
            modifiedDate = System.currentTimeMillis()
            publicKey = slackKey {
                this.keybytes.addAll(publicKeyChannel.map {
                    sKByteArrayElement {
                        this.byte = it.toInt()
                    }
                })
            }
        }.toDBChannel()
        return channel
    }

    override fun registerChangeInChannelMembers(request: SKChannelMember): Flow<SKChannelMemberChangeSnapshot> {
        return channelsDataSource.getChannelMemberChangeStream(request.workspaceId, request.memberId).map { skChannel ->
            SKChannelMemberChangeSnapshot.newBuilder()
                .apply {
                    skChannel.first?.toGRPC()?.let { skChannel1 ->
                        previous = skChannel1
                    }
                    skChannel.second?.toGRPC()?.let { skChannel1 ->
                        latest = skChannel1
                    }
                }
                .build()
        }
    }

    override fun registerChangeInChannels(request: SKChannelRequest): Flow<SKChannelChangeSnapshot> {
        val authData = AUTH_CONTEXT_KEY.get()
        return channelsDataSource.getChannelChangeStream(request.workspaceId).map { skChannel ->
            SKChannelChangeSnapshot.newBuilder()
                .apply {
                    skChannel.first?.toGRPC()?.let { skChannel1 ->
                        val isMember =
                            channelMemberDataSource.isMember(authData.userId, request.workspaceId, skChannel1.uuid)
                        if (isMember) {
                            previous = skChannel1
                        }
                    }
                    skChannel.second?.toGRPC()?.let { skChannel1 ->
                        val isMember =
                            channelMemberDataSource.isMember(authData.userId, request.workspaceId, skChannel1.uuid)
                        if (isMember) {
                            latest = skChannel1
                        }
                    }
                }
                .build()
        }
    }

    override fun registerChangeInDMChannels(request: SKChannelRequest): Flow<SKDMChannelChangeSnapshot> {
        return channelsDataSource.getDMChannelChangeStream(request.workspaceId).map { skChannel ->
            SKDMChannelChangeSnapshot.newBuilder()
                .apply {
                    skChannel.first?.toGRPC()?.let { skMessage ->
                        previous = skMessage
                    }
                    skChannel.second?.toGRPC()?.let { skMessage ->
                        latest = skMessage
                    }
                }
                .build()
        }
    }
}

private fun EncryptedData.toSlackKey(): SKEncryptedMessage {
    return SKEncryptedMessage.newBuilder()
        .setFirst(first)
        .setSecond(second)
        .build()
}

fun SKUserPublicKey.toPublicKey(): dev.baseio.security.PublicKey {
    return JVMKeyStoreRsaUtils.getPublicKeyFromBytes(this.keyBytes)
}

private fun SKChannelMember.toDBMember(): SkChannelMember {
    return SkChannelMember(
        this.workspaceId,
        this.channelId,
        this.memberId,
        this.channelPrivateKey.toSKEncryptedMessage()
    ).apply {
        this@toDBMember.uuid?.takeIf { it.isNotEmpty() }?.let {
            this.uuid = this@toDBMember.uuid
        }
    }
}

fun SKEncryptedMessage.toSKEncryptedMessage(): dev.baseio.slackserver.data.models.SKEncryptedMessage {
    return dev.baseio.slackserver.data.models.SKEncryptedMessage(
        this.first,
        this.second
    )
}


private fun SlackKey.toSKUserPublicKey(): SKUserPublicKey {
    return SKUserPublicKey(this.keybytesList.map { it.byte.toByte() }.toByteArray())
}

fun SkChannelMember.toGRPC(): SKChannelMember {
    val member = this
    return sKChannelMember {
        this.uuid = member.uuid
        this.channelId = member.channelId
        this.workspaceId = member.workspaceId
        this.memberId = member.memberId
        this.channelPrivateKey = sKEncryptedMessage {
            this.first = member.channelEncryptedPrivateKey!!.first
            this.second = member.channelEncryptedPrivateKey.second
        }
    }
}

fun SKDMChannel.toDBChannel(
): SkChannel.SkDMChannel {
    return SkChannel.SkDMChannel(
        this.uuid,
        this.workspaceId,
        this.senderId,
        this.receiverId,
        createdDate,
        modifiedDate,
        isDeleted,
        SKUserPublicKey(keyBytes = this.publicKey.keybytesList.map { it.byte.toByte() }.toByteArray())
    )
}

fun SKChannel.toDBChannel(): SkChannel.SkGroupChannel {
    return SkChannel.SkGroupChannel(
        this.uuid.takeIf { !it.isNullOrEmpty() } ?: UUID.randomUUID().toString(),
        this.workspaceId,
        this.name,
        createdDate,
        modifiedDate,
        avatarUrl,
        isDeleted,
        SKUserPublicKey(keyBytes = this.publicKey.keybytesList.map { it.byte.toByte() }.toByteArray())
    )
}

fun SkChannel.SkGroupChannel.toGRPC(): SKChannel {
    return SKChannel.newBuilder()
        .setUuid(this.uuid)
        .setAvatarUrl(this.avatarUrl ?: "")
        .setName(this.name)
        .setCreatedDate(this.createdDate)
        .setWorkspaceId(this.workspaceId)
        .setModifiedDate(this.modifiedDate)
        .setPublicKey(SlackKey.newBuilder().addAllKeybytes(this.publicKey.keyBytes.map {
            sKByteArrayElement {
                this.byte = it.toInt()
            }
        }).build())
        .build()
}

fun SkChannel.SkDMChannel.toGRPC(): SKDMChannel {
    return SKDMChannel.newBuilder()
        .setUuid(this.uuid)
        .setCreatedDate(this.createdDate)
        .setModifiedDate(this.modifiedDate)
        .setIsDeleted(this.deleted)
        .setReceiverId(this.receiverId)
        .setSenderId(this.senderId)
        .setWorkspaceId(this.workspaceId)
        .setPublicKey(SlackKey.newBuilder().addAllKeybytes(this.publicKey.keyBytes.map {
            sKByteArrayElement {
                this.byte = it.toInt()
            }
        }).build())
        .build()
}

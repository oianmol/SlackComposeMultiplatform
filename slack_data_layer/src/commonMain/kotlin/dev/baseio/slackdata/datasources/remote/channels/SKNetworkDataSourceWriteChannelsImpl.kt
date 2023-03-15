package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.security.CapillaryInstances
import dev.baseio.slackdata.common.kmSKByteArrayElement
import dev.baseio.slackdata.protos.*
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceWriteChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.withContext

class SKNetworkDataSourceWriteChannelsImpl(
    private val grpcCalls: IGrpcCalls,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKNetworkDataSourceWriteChannels {
    override suspend fun createChannel(params: DomainLayerChannels.SKChannel): Result<DomainLayerChannels.SKChannel> {
        return withContext(coroutineDispatcherProvider.io) {
            kotlin.runCatching {
                when (params) {
                    is DomainLayerChannels.SKChannel.SkDMChannel -> {
                        grpcCalls.saveDMChannel(kmSKDMChannel {
                            uuid = params.uuid
                            workspaceId = params.workId
                            createdDate = params.createdDate
                            modifiedDate = params.modifiedDate
                            this.senderId = params.senderId
                            this.receiverId = params.receiverId
                            this.isDeleted = params.deleted
                            this.publicKey = CapillaryInstances.getInstance(params.uuid)
                                .publicKey().encoded.toKMSlackPublicKey()
                        }).mapToDomainSkChannel()
                    }

                    is DomainLayerChannels.SKChannel.SkGroupChannel -> {
                        grpcCalls.savePublicChannel(kmSKChannel {
                            uuid = params.uuid
                            workspaceId = params.workId
                            name = params.name
                            createdDate = params.createdDate
                            modifiedDate = params.modifiedDate
                            avatarUrl = params.avatarUrl
                            this.isDeleted = params.deleted
                            this.publicKey = CapillaryInstances.getInstance(params.uuid)
                                .publicKey().encoded.toKMSlackPublicKey()
                        }).mapToDomainSkChannel()
                    }
                }
            }
        }


    }
}

fun DomainLayerUsers.SKSlackKey.toByteArray(): ByteArray {
    return this.keyBytes
}

fun ByteArray.toKMSlackPublicKey(): KMSlackKey {
    return kmSlackKey {
        this.keybytesList.addAll(this@toKMSlackPublicKey.map {
            kmSKByteArrayElement {
                byte = it.toInt()
            }
        })
    }
}

fun ByteArray.toSKUserPublicKey(): DomainLayerUsers.SKSlackKey {
    return kmSlackKey {
        this.keybytesList.addAll(this@toSKUserPublicKey.map {
            kmSKByteArrayElement {
                byte = it.toInt()
            }
        })
    }.toUserPublicKey()
}

fun KMSKChannel.mapToDomainSkChannel(): DomainLayerChannels.SKChannel {
    val params = this
    return DomainLayerChannels.SKChannel.SkGroupChannel(
        uuid = params.uuid,
        workId = params.workspaceId,
        name = params.name,
        createdDate = params.createdDate,
        modifiedDate = params.modifiedDate,
        avatarUrl = params.avatarUrl,
        deleted = params.isDeleted,
        channelPublicKey = params.publicKey.toUserPublicKey(),
    )
}

fun KMSlackKey.toUserPublicKey(): DomainLayerUsers.SKSlackKey {
    return DomainLayerUsers.SKSlackKey(keyBytes = this.keybytesList.map { it.byte.toByte() }
        .toByteArray())
}

fun DomainLayerUsers.SKSlackKey.toSlackKey(): KMSlackKey {
    return kmSlackKey {
        this.keybytesList.addAll(keyBytes.map {
            kmSKByteArrayElement {
                byte = it.toInt()
            }
        })
    }
}

fun KMSKDMChannel.mapToDomainSkChannel(): DomainLayerChannels.SKChannel {
    val params = this
    return DomainLayerChannels.SKChannel.SkDMChannel(
        uuid = params.uuid,
        workId = params.workspaceId,
        createdDate = params.createdDate,
        modifiedDate = params.modifiedDate,
        deleted = params.isDeleted,
        senderId = params.senderId,
        receiverId = params.receiverId,
        channelPublicKey = params.publicKey.toUserPublicKey()
    )
}

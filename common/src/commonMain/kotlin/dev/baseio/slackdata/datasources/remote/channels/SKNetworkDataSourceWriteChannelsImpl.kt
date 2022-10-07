package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.KMSKChannel
import dev.baseio.slackdata.protos.kmSKChannel
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceWriteChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class SKNetworkDataSourceWriteChannelsImpl(private val grpcCalls: GrpcCalls) : SKNetworkDataSourceWriteChannels {
  override suspend fun createChannel(params: DomainLayerChannels.SKChannel): DomainLayerChannels.SKChannel {
    return grpcCalls.saveChannel(kmSKChannel {
      uuid = params.uuid
      workspaceId = params.workspaceId
      name = params.name
      createdDate = params.createdDate
      modifiedDate = params.modifiedDate
      isMuted = params.isMuted
      isPrivate = params.isPrivate
      isStarred = params.isStarred
      isShareOutSide = params.isShareOutSide
      isOneToOne = params.isOneToOne
      avatarUrl = params.avatarUrl
    }).mapToDomainSkChannel()
  }
}

fun KMSKChannel.mapToDomainSkChannel(): DomainLayerChannels.SKChannel {
  val params = this
  return DomainLayerChannels.SKChannel(
    uuid = params.uuid,
    workspaceId = params.workspaceId,
    name = params.name,
    createdDate = params.createdDate,
    modifiedDate = params.modifiedDate,
    isMuted = params.isMuted,
    isPrivate = params.isPrivate,
    isStarred = params.isStarred,
    isShareOutSide = params.isShareOutSide,
    isOneToOne = params.isOneToOne,
    avatarUrl = params.avatarUrl
  )
}

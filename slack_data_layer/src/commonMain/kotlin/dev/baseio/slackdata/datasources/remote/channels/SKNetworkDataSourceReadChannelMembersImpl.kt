package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.protos.KMSKChannelMember
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannelMembers
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest

class SKNetworkDataSourceReadChannelMembersImpl(private val grpcCalls: IGrpcCalls) :
  SKNetworkDataSourceReadChannelMembers {
  override suspend fun fetchChannelMembers(request: UseCaseWorkspaceChannelRequest): Result<List<DomainLayerChannels.SkChannelMember>> {
    return kotlin.runCatching {
      val channelMembers = grpcCalls.fetchChannelMembers(request)
      channelMembers.membersList.map {
        it.toDomain()
      }
    }
  }
}

fun KMSKChannelMember.toDomain(): DomainLayerChannels.SkChannelMember {
  return DomainLayerChannels.SkChannelMember(this.uuid, this.workspaceId, this.channelId, this.memberId,this.channelPrivateKey.toDomainSKEncryptedMessage())
}

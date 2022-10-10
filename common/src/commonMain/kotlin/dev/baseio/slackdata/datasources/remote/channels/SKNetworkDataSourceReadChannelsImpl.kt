package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.flow.*

class SKNetworkDataSourceReadChannelsImpl(private val grpcCalls: GrpcCalls) : SKNetworkDataSourceReadChannels {
  override fun fetchChannels(workspaceId: String): Flow<List<DomainLayerChannels.SKChannel>> {
    return grpcCalls.getChannels(workspaceId).map { channels->
      channels.channelsList.map {
        it.mapToDomainSkChannel()
      }
    }
  }
}
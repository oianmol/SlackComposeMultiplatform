package dev.baseio.slackdata.datasources.remote.channels

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class SKNetworkDataSourceReadChannelsImpl(
  private val grpcCalls: IGrpcCalls,
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKNetworkDataSourceReadChannels {

  override fun listenToChangeInChannels(workspaceId: String): Flow<Pair<DomainLayerChannels.SKChannel?, DomainLayerChannels.SKChannel?>> {
    return combine(
      grpcCalls.listenToChangeInChannels(workspaceId),
      grpcCalls.listenToChangeInDMChannels(workspaceId)
    ) { public, dmChannel ->
      Pair(
        if (public.hasPrevious()) public.previous.mapToDomainSkChannel() else null,
        if (public.hasLatest()) public.latest.mapToDomainSkChannel() else null
      )
      Pair(
        if (dmChannel.hasPrevious()) dmChannel.previous.mapToDomainSkChannel() else null,
        if (dmChannel.hasLatest()) dmChannel.latest.mapToDomainSkChannel() else null
      )
    }.catch {
      // notify upstream for these errors
    }
  }

  override suspend fun fetchChannels(
    workspaceId: String,
    offset: Int,
    limit: Int
  ): Result<List<DomainLayerChannels.SKChannel>> {
    return withContext(coroutineDispatcherProvider.io) {
      kotlin.runCatching {
        grpcCalls.getPublicChannels(workspaceId, offset, limit).run {
          this.channelsList.map {
            it.mapToDomainSkChannel()
          }
        } +
        grpcCalls.getAllDMChannels(workspaceId, offset, limit).run {
          this.channelsList.map {
            it.mapToDomainSkChannel()
          }
        }
      }
    }

  }
}
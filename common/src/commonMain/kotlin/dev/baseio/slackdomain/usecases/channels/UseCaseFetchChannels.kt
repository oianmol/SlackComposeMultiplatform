package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceCreateChannels
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceReadChannels
import dev.baseio.slackdomain.datasources.remote.channels.SKNetworkDataSourceWriteChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.BaseUseCase
import kotlinx.coroutines.flow.*

class UseCaseFetchChannels(
  private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels,
  private val skNetworkDataSourceReadChannels: SKNetworkDataSourceReadChannels,
  private val skLocalDataSourceWriteChannels: SKLocalDataSourceCreateChannels
) : BaseUseCase<List<DomainLayerChannels.SKChannel>, String> {

  override fun performStreaming(params: String): Flow<List<DomainLayerChannels.SKChannel>> {
    return skNetworkDataSourceReadChannels.fetchChannels(workspaceId = params).mapLatest { skChannelList ->
      skChannelList.map { skChannel ->
        skLocalDataSourceWriteChannels.saveChannel(skChannel)
      }
    }.flatMapLatest {
      skLocalDataSourceReadChannels.fetchChannels(params)
    }.catch {
      skLocalDataSourceReadChannels.fetchChannels(params)
    }
  }

}

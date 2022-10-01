package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.BaseUseCase
import kotlinx.coroutines.flow.Flow

class UseCaseFetchChannels(
  private val skDataSourceChannels: SKDataSourceChannels,
) : BaseUseCase<List<DomainLayerChannels.SKChannel>, String> {

  override fun performStreaming(params: String): Flow<List<DomainLayerChannels.SKChannel>> {
    return skDataSourceChannels.fetchChannels(params)
  }

}

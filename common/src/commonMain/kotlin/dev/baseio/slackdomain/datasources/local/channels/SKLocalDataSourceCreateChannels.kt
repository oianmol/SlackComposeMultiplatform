package dev.baseio.slackdomain.datasources.local.channels


import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers

interface SKLocalDataSourceCreateChannels { // TODO move to SKLocalDataSourcewriteChannels
  suspend fun saveChannel(params: DomainLayerChannels.SKChannel): DomainLayerChannels.SKChannel
  suspend fun saveOneToOneChannels(params: List<DomainLayerUsers.SKUser>)
  suspend fun saveChannels(channels: MutableList<DomainLayerChannels.SKChannel>)
}


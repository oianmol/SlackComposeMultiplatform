package dev.baseio.slackdomain.datasources.local.channels


import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers

interface SKLocalDataSourceCreateChannels {
  suspend fun saveChannel(params: DomainLayerChannels.SKChannel): DomainLayerChannels.SKChannel
}


package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceCreateChannels
import dev.baseio.slackdomain.usecases.BaseUseCase

class UseCaseCreateOneToOneChannel(private val SKDataSourceCreateChannels: SKDataSourceCreateChannels) :
  BaseUseCase<Unit, List<DomainLayerUsers.SKUser>> {
  override suspend fun perform(params: List<DomainLayerUsers.SKUser>) {
    return SKDataSourceCreateChannels.saveOneToOneChannels(params)
  }
}
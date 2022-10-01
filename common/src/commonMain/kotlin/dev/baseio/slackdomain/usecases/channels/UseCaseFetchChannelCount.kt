package dev.baseio.slackdomain.usecases.channels

import dev.baseio.slackdomain.datasources.local.channels.SKDataSourceChannels
import dev.baseio.slackdomain.usecases.BaseUseCase

class UseCaseFetchChannelCount(private val skDataSourceChannels: SKDataSourceChannels) :
  BaseUseCase<Int, String> {
  override suspend fun perform(workspaceId:String): Int {
    return skDataSourceChannels.channelCount(workspaceId).toInt()
  }
}
package dev.baseio.slackdomain.usecases.auth

import dev.baseio.slackdomain.datasources.local.SKLocalDatabaseSource
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource

class UseCaseLogout(
  private val skKeyValueData: SKLocalKeyValueSource,
  private val slackChannelDao: SKLocalDatabaseSource,
) {
  operator fun invoke() {
    skKeyValueData.clear()
    slackChannelDao.clear()
  }
}
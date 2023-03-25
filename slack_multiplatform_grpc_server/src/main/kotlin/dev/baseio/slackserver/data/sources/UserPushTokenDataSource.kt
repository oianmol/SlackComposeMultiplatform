package dev.baseio.slackserver.data.sources

import dev.baseio.slackserver.data.models.SKUserPushToken

interface UserPushTokenDataSource {
  suspend fun getPushTokensFor(userIds: List<String>): List<SKUserPushToken>
  suspend fun savePushToken(toSkUserPushToken: SKUserPushToken)
}
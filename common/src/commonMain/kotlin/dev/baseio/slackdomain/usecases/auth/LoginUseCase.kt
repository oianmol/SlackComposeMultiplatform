package dev.baseio.slackdomain.usecases.auth

import SKKeyValueData
import dev.baseio.slackdomain.AUTH_TOKEN
import dev.baseio.slackdomain.LOGGED_IN_ID
import dev.baseio.slackdomain.datasources.remote.auth.SKAuthNetworkDataSource

class LoginUseCase(
  private val SKAuthNetworkDataSource: SKAuthNetworkDataSource,
  private val skKeyValueData: SKKeyValueData
) {
  suspend operator fun invoke(email: String, password: String, workspaceId: String) {
    val result = SKAuthNetworkDataSource.login(email, password, workspaceId).getOrThrow()
    skKeyValueData.save(AUTH_TOKEN, result.token)
    val user = SKAuthNetworkDataSource.getLoggedInUser().getOrThrow()
    skKeyValueData.save(LOGGED_IN_ID, user.uuid)
  }
}
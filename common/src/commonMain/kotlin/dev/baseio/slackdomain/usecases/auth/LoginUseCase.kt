package dev.baseio.slackdomain.usecases.auth

import SKKeyValueData
import dev.baseio.slackdomain.AUTH_TOKEN
import dev.baseio.slackdomain.datasources.remote.auth.AuthDataSource

class LoginUseCase(
  private val authDataSource: AuthDataSource,
  private val skKeyValueData: SKKeyValueData
) {
  suspend operator fun invoke(email: String, password: String, workspaceId: String) {
    val result = authDataSource.login(email, password, workspaceId).getOrThrow()
    skKeyValueData.save(AUTH_TOKEN, result.token)
  }
}
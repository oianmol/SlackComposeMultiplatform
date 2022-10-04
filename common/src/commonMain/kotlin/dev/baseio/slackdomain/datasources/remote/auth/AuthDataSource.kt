package dev.baseio.slackdomain.datasources.remote.auth

interface AuthDataSource {
  suspend fun login(email: String, password: String, workspaceId: String): Result<AuthResult>
}
package dev.baseio.slackdomain.datasources.remote.auth

import dev.baseio.slackdata.protos.KMSKAuthResult

interface AuthNetworkDataSource {
  suspend fun login(email: String, password: String, workspaceId: String): Result<KMSKAuthResult>
}
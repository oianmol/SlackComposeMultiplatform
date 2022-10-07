package dev.baseio.slackdomain.datasources.remote.auth

import dev.baseio.slackdata.protos.KMSKAuthResult
import dev.baseio.slackdata.protos.KMSKUser

interface SKAuthNetworkDataSource {
  suspend fun login(email: String, password: String, workspaceId: String): Result<KMSKAuthResult>
  suspend fun getLoggedInUser(): Result<KMSKUser>
}
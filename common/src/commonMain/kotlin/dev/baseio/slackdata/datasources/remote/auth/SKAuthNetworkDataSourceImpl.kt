package dev.baseio.slackdata.datasources.remote.auth

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.KMSKAuthResult
import dev.baseio.slackdata.protos.KMSKUser
import dev.baseio.slackdata.protos.kmSKAuthUser
import dev.baseio.slackdata.protos.kmSKUser
import dev.baseio.slackdomain.datasources.remote.auth.SKAuthNetworkDataSource

class SKAuthNetworkDataSourceImpl(private val grpcCalls: GrpcCalls) : SKAuthNetworkDataSource {
  override suspend fun login(email: String, password: String, workspaceId: String): Result<KMSKAuthResult> {
    return kotlin.runCatching {
      grpcCalls.login(kmSKAuthUser {
        this.email = email
        this.password = password
        this.user = kmSKUser {
          this.workspaceId = workspaceId
        }
      })
    }
  }

  override suspend fun getLoggedInUser(): Result<KMSKUser> {
    return kotlin.runCatching {
      grpcCalls.currentLoggedInUser()
    }
  }
}
package dev.baseio.slackdata.datasources.remote.auth

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.KMSKAuthResult
import dev.baseio.slackdata.protos.kmSKAuthUser
import dev.baseio.slackdata.protos.kmSKUser
import dev.baseio.slackdomain.datasources.remote.auth.AuthDataSource

class AuthDataSourceImpl(private val grpcCalls: GrpcCalls) : AuthDataSource {
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
}
package dev.baseio.grpc


import SKKeyValueData
import dev.baseio.slackdata.protos.*
import dev.baseio.slackdomain.AUTH_TOKEN
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMChannel
import kotlinx.coroutines.flow.Flow
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMMetadata


class GrpcCalls(private val skKeyValueData: SKKeyValueData) {
  companion object {
    const val address = "localhost"
    const val port = 17600
    const val AUTHENTICATION_TOKEN_KEY = "Authorization"
  }

  val grpcChannel by lazy {
    KMChannel.Builder
      .forAddress(address, port)
      .usePlaintext()
      .build()
  }

  val workspacesStub by lazy {
    KMWorkspaceServiceStub(grpcChannel)
  }

  val channelsStub by lazy {
    KMChannelsServiceStub(grpcChannel)
  }

  val authStub by lazy {
    KMAuthServiceStub(grpcChannel)
  }

  val usersStub by lazy {
    KMUsersServiceStub(grpcChannel)
  }

  val messagingStub by lazy {
    KMMessagesServiceStub(grpcChannel)
  }

  fun streamUsersForWorkspaceId(workspace: String, token: String? = skKeyValueData.get(AUTH_TOKEN)): Flow<KMSKUsers> {
    return usersStub.getUsers(kmSKWorkspaceChannelRequest { workspaceId = workspace }, fetchToken(token))
  }

  suspend fun currentLoggedInUser(token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKUser {
    return usersStub.currentLoggedInUser(kmEmpty { }, fetchToken(token))
  }

  suspend fun register(kmskAuthUser: KMSKAuthUser, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKAuthResult {
    return authStub.register(kmskAuthUser, fetchToken(token))
  }

  suspend fun forgotPassword(kmskAuthUser: KMSKAuthUser, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKUser {
    return authStub.forgotPassword(kmskAuthUser, fetchToken(token))
  }

  suspend fun resetPassword(kmskAuthUser: KMSKAuthUser, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKUser {
    return authStub.resetPassword(kmskAuthUser, fetchToken(token))
  }

  suspend fun findWorkspaceByName(name: String, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKWorkspace {
    return workspacesStub.findWorkspaceForName(kmSKFindWorkspacesRequest {
      this.name = name
    }, fetchToken(token))
  }

  suspend fun findWorkspacesForEmail(email: String, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKWorkspaces {
    return workspacesStub.findWorkspacesForEmail(kmSKFindWorkspacesRequest {
      this.email = email
    }, fetchToken(token))
  }

  suspend fun login(kmskAuthUser: KMSKAuthUser, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKAuthResult {
    return authStub.login(kmskAuthUser, fetchToken(token))
  }

  fun getWorkspaces(token: String? = skKeyValueData.get(AUTH_TOKEN)): Flow<KMSKWorkspaces> {
    return workspacesStub.getWorkspaces(kmEmpty { }, fetchToken(token))
  }

  suspend fun saveWorkspace(workspace: KMSKWorkspace, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKWorkspace {
    return workspacesStub.saveWorkspace(workspace, fetchToken(token))
  }

  fun getChannels(workspaceIdentifier: String, token: String? = skKeyValueData.get(AUTH_TOKEN)): Flow<KMSKChannels> {
    return channelsStub.getChannels(kmSKChannelRequest {
      workspaceId = workspaceIdentifier
    }, fetchToken(token))
  }

  suspend fun saveChannel(kmChannel: KMSKChannel, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKChannel {
    return channelsStub.saveChannel(kmChannel, fetchToken(token))
  }

  fun listenMessages(
    workspaceChannelRequest: KMSKWorkspaceChannelRequest,
    token: String? = skKeyValueData.get(AUTH_TOKEN)
  ): Flow<KMSKMessages> {
    return messagingStub.getMessages(workspaceChannelRequest, fetchToken(token))
  }

  suspend fun sendMessage(kmskMessage: KMSKMessage, token: String? = skKeyValueData.get(AUTH_TOKEN)): KMSKMessage {
    return messagingStub.saveMessage(kmskMessage, fetchToken(token))
  }


  fun fetchToken(token: String?): KMMetadata {
    return KMMetadata().apply {
      if (token != null) {
        set(AUTHENTICATION_TOKEN_KEY, "Bearer $token")
      }
    }
  }

  fun clearAuth() {
    skKeyValueData.clear()
  }
}


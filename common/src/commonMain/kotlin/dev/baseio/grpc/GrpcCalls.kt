package dev.baseio.grpc


import SKKeyValueData
import dev.baseio.slackdata.protos.*
import dev.baseio.slackdomain.AUTH_TOKEN
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMChannel
import kotlinx.coroutines.flow.Flow
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMMetadata


class GrpcCalls(private val skKeyValueData: SKKeyValueData) {
  companion object {
    const val address = "192.168.1.5"
    const val port = 17600
    const val AUTHENTICATION_TOKEN_KEY = "token"
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

  suspend fun register(kmskAuthUser: KMSKAuthUser): KMSKAuthResult {
    return authStub.register(kmskAuthUser)
  }

  suspend fun forgotPassword(kmskAuthUser: KMSKAuthUser): KMSKUser {
    return authStub.forgotPassword(kmskAuthUser)
  }

  suspend fun resetPassword(kmskAuthUser: KMSKAuthUser): KMSKUser {
    return authStub.resetPassword(kmskAuthUser)
  }

  suspend fun findWorkspaceByName(name: String): KMSKWorkspace {
    return workspacesStub.findWorkspaceForName(kmSKFindWorkspacesRequest {
      this.name = name
    })
  }

  suspend fun findWorkspacesForEmail(email: String): KMSKWorkspaces {
    return workspacesStub.findWorkspacesForEmail(kmSKFindWorkspacesRequest {
      this.email = email
    })
  }

  suspend fun login(kmskAuthUser: KMSKAuthUser): KMSKAuthResult {
    return authStub.login(kmskAuthUser)
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
        set(AUTHENTICATION_TOKEN_KEY, token)
      }
    }
  }
}


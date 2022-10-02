package dev.baseio


import dev.baseio.slackdata.protos.*
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMChannel
import kotlinx.coroutines.flow.Flow
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMMetadata

const val address = "localhost"
const val port = 17600
const val AUTHENTICATION_TOKEN_KEY = "token"

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
val usersStub by lazy {
  KMUsersServiceStub(grpcChannel)
}
val messagingStub by lazy {
  KMMessagesServiceStub(grpcChannel)
}

fun getWorkspaces(token: String? = null): Flow<KMSKWorkspaces> {
  return workspacesStub.getWorkspaces(kmEmpty { }, fetchToken(token))
}

suspend fun saveWorkspace(workspace: KMSKWorkspace, token: String? = null): KMSKWorkspace {
  return workspacesStub.saveWorkspace(workspace, fetchToken(token))
}

fun getChannels(workspaceIdentifier: String, token: String? = null): Flow<KMSKChannels> {
  return channelsStub.getChannels(kmSKChannelRequest {
    workspaceId = workspaceIdentifier
  }, fetchToken(token))
}

suspend fun saveChannel(kmChannel: KMSKChannel, token: String? = null): KMSKChannel {
  return channelsStub.saveChannel(kmChannel, fetchToken(token))
}

fun listenMessages(workspaceChannelRequest: KMSKWorkspaceChannelRequest, token: String? = null): Flow<KMSKMessages> {
  return messagingStub.getMessages(workspaceChannelRequest, fetchToken(token))
}

suspend fun sendMessage(kmskMessage: KMSKMessage, token: String? = null): KMSKMessage {
  return messagingStub.saveMessage(kmskMessage, fetchToken(token))
}


fun fetchToken(token: String?): KMMetadata {
  return KMMetadata().apply {
    if (token != null) {
      set(AUTHENTICATION_TOKEN_KEY, token)
    }
  }
}
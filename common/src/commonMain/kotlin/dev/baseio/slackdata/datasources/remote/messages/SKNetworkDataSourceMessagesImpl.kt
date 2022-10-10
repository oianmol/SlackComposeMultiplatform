package dev.baseio.slackdata.datasources.remote.messages

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.KMSKMessage
import dev.baseio.slackdata.protos.kmSKMessage
import dev.baseio.slackdata.protos.kmSKWorkspaceChannelRequest
import dev.baseio.slackdomain.datasources.remote.messages.SKNetworkDataSourceMessages
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.auth.toSKUser
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class SKNetworkDataSourceMessagesImpl(private val grpcCalls: GrpcCalls) : SKNetworkDataSourceMessages {

  override fun getMessages(request: UseCaseChannelRequest): Flow<List<DomainLayerMessages.SKMessage>> {
    return grpcCalls.listenMessages(kmSKWorkspaceChannelRequest {
      workspaceId = request.workspaceId
      channelId = request.uuid
    }).mapLatest { message->
      message.messagesList.map {  it.toDomainLayerMessage() }
    }
  }

  override suspend fun sendMessage(params: DomainLayerMessages.SKMessage): DomainLayerMessages.SKMessage {
    return grpcCalls.sendMessage(kmSKMessage {
      uuid = params.uuid
      workspaceId = params.workspaceId
      channelId = params.channelId
      text = params.message
      `receiver` = params.receiver
      sender = params.sender
      createdDate = params.createdDate
      modifiedDate = params.modifiedDate
    }).toDomainLayerMessage()
  }
}

fun KMSKMessage.toDomainLayerMessage(): DomainLayerMessages.SKMessage {
  val params = this
  return DomainLayerMessages.SKMessage(
    uuid = params.uuid,
    workspaceId = params.workspaceId,
    channelId = params.channelId,
    message = params.text,
    `receiver` = params.receiver,
    sender = params.sender,
    createdDate = params.createdDate,
    modifiedDate = params.modifiedDate,
    senderInfo = params.senderInfo.toSKUser()
  )
}

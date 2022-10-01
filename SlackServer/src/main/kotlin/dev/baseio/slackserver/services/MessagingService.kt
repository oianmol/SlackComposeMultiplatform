package dev.baseio.slackserver.services

import database.SkMessage
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.data.impl.MessagesDataSourceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext

class MessagingService(
  coroutineContext: CoroutineContext = Dispatchers.IO,
  private val messagesDataSource: MessagesDataSourceImpl,
) : MessagesServiceGrpcKt.MessagesServiceCoroutineImplBase(coroutineContext) {
  override suspend fun saveMessage(request: SKMessage): SKMessage {
    return messagesDataSource
      .saveMessage(request.toDBMessage())
      .toGrpc()
  }

  override fun getMessages(request: SKWorkspaceChannelRequest): Flow<SKMessages> {
    return messagesDataSource.getMessages(workspaceId = request.workspaceId, channelId = request.channelId)
      .map { query ->
        val skMessages = query.executeAsList().map { skMessage ->
          skMessage.toGrpc()
        }
        SKMessages.newBuilder()
          .addAllMessages(skMessages)
          .build()
      }.catch { throwable ->
      throwable.printStackTrace()
      emit(SKMessages.newBuilder().build())
    }
  }
}

private fun SkMessage.toGrpc(): SKMessage {
  return SKMessage.newBuilder()
    .setUuid(this.uuid)
    .setCreatedDate(this.createdDate.toLong())
    .setModifiedDate(this.modifiedDate.toLong())
    .setWorkspaceId(this.workspaceId)
    .setChannelId(this.channelId)
    .setReceiver(this.receiver_)
    .setSender(this.sender)
    .setText(this.message)
    .build()
}

private fun SKMessage.toDBMessage(): SkMessage {
  return SkMessage(
    uuid = this.uuid,
    workspaceId = this.workspaceId,
    channelId,
    text,
    receiver,
    sender,
    createdDate.toInt(),
    modifiedDate.toInt()
  )
}

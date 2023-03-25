package dev.baseio.slackserver.services

import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.communications.NotificationType
import dev.baseio.slackserver.communications.PNSender
import dev.baseio.slackserver.data.sources.MessagesDataSource
import dev.baseio.slackserver.data.models.SkMessage
import dev.baseio.slackserver.services.interceptors.AUTH_CONTEXT_KEY
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class MessagingService(
    coroutineContext: CoroutineContext = Dispatchers.IO,
    private val messagesDataSource: MessagesDataSource,
    private val pushNotificationForMessages: PNSender<SkMessage>,

    ) : MessagesServiceGrpcKt.MessagesServiceCoroutineImplBase(coroutineContext) {


  override suspend fun updateMessage(request: SKMessage): SKMessage {
    val authData = AUTH_CONTEXT_KEY.get()
    return messagesDataSource.updateMessage(request.toDBMessage())?.toGrpc()?.also {
      pushNotificationForMessages.sendPushNotifications(request.toDBMessage(), authData.userId,NotificationType.NEW_MESSAGE)
    }
      ?: throw StatusException(Status.NOT_FOUND)
  }

  override suspend fun saveMessage(request: SKMessage): SKMessage {
    val authData = AUTH_CONTEXT_KEY.get()
    return messagesDataSource
      .saveMessage(request.toDBMessage())
      .toGrpc().also {
        pushNotificationForMessages.sendPushNotifications(
          request = request.toDBMessage(),
          senderUserId = authData.userId, NotificationType.NEW_MESSAGE
        )
      }
  }

  override fun registerChangeInMessage(request: SKWorkspaceChannelRequest): Flow<SKMessageChangeSnapshot> {
    return messagesDataSource.registerForChanges(request).map {
      SKMessageChangeSnapshot.newBuilder()
        .apply {
          it.first?.toGrpc()?.let { skMessage ->
            previous = skMessage
          }
          it.second?.toGrpc()?.let { skMessage ->
            latest = skMessage
          }
        }
        .build()
    }.catch {
      it.printStackTrace()
    }
  }

  override suspend fun getMessages(request: SKWorkspaceChannelRequest): SKMessages {
    val messages = messagesDataSource.getMessages(
      workspaceId = request.workspaceId,
      channelId = request.channelId,
      request.paged.limit,
      request.paged.offset
    ).map { skMessage ->
      skMessage.toGrpc()
    }
    return SKMessages.newBuilder()
      .addAllMessages(messages)
      .build()
  }


}

private fun SkMessage.toGrpc(): SKMessage {
  return SKMessage.newBuilder()
    .setUuid(this.uuid)
    .setCreatedDate(this.createdDate)
    .setModifiedDate(this.modifiedDate)
    .setWorkspaceId(this.workspaceId)
    .setChannelId(this.channelId)
    .setSender(this.sender)
    .setText(SKEncryptedMessage.parseFrom(this.message))
    .setIsDeleted(this.isDeleted)
    .build()
}

private fun SKMessage.toDBMessage(uuid: String = UUID.randomUUID().toString()): SkMessage {
  return SkMessage(
    uuid = this.uuid.takeIf { !it.isNullOrEmpty() } ?: uuid,
    workspaceId = this.workspaceId,
    channelId,
    text.toByteArray(),
    sender,
    createdDate,
    modifiedDate,
    isDeleted = this.isDeleted
  )
}

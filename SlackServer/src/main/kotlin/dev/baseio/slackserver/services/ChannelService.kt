package dev.baseio.slackserver.services

import database.SkChannel
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.data.ChannelsDataSource
import dev.baseio.slackserver.services.interceptors.AUTH_CONTEXT_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.coroutines.CoroutineContext

class ChannelService(
  coroutineContext: CoroutineContext = Dispatchers.IO,
  private val channelsDataSource: ChannelsDataSource
) :
  ChannelsServiceGrpcKt.ChannelsServiceCoroutineImplBase(coroutineContext) {

  override suspend fun saveChannel(request: SKChannel): SKChannel {
    val clientId = AUTH_CONTEXT_KEY.get()
    return channelsDataSource.insertChannel(request.toDBChannel()).toGRPC()
  }

  override fun getChannels(request: SKChannelRequest): Flow<SKChannels> {
    return channelsDataSource.getChannels(request.workspaceId).map {
      val channels = it.executeAsList().map { dbChannel ->
        dbChannel.toGRPC()
      }
      SKChannels.newBuilder()
        .addAllChannels(channels)
        .build()
    }
  }
}

fun SKChannel.toDBChannel(
  workspaceId: String = UUID.randomUUID().toString(),
  channelId: String = UUID.randomUUID().toString()
): SkChannel {
  return SkChannel(
    this.uuid.takeIf { !it.isNullOrEmpty() } ?: channelId,
    this.workspaceId ?: workspaceId,
    this.name,
    createdDate.toInt(),
    modifiedDate.toInt(),
    isMuted.oneOrZero(), isPrivate.oneOrZero(),
    isStarred.oneOrZero(), isShareOutSide.oneOrZero(),
    isOneToOne.oneOrZero(),
    avatarUrl
  )
}

private fun Boolean.oneOrZero(): Int {
  return if (this) 1 else 0
}

fun SkChannel.toGRPC(): SKChannel {
  return SKChannel.newBuilder()
    .setUuid(this.uuid)
    .setAvatarUrl(this.avatarUrl)
    .setName(this.name)
    .setCreatedDate(this.createdDate.toLong())
    .setIsMuted(this.isMuted == 1)
    .setIsPrivate(this.isPrivate == 1)
    .setIsStarred(this.isStarred == 1)
    .setIsOneToOne(this.isOneToOne == 1)
    .setIsShareOutSide(this.isShareOutSide == 1)
    .setWorkspaceId(this.workspaceId)
    .setModifiedDate(this.modifiedDate.toLong())
    .build()
}

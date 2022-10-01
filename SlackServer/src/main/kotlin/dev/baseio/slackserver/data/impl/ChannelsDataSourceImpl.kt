package dev.baseio.slackserver.data.impl

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import database.SkChannel
import dev.baseio.SlackCloneDB
import dev.baseio.slackserver.data.ChannelsDataSource
import kotlinx.coroutines.flow.Flow

class ChannelsDataSourceImpl(private val slackCloneDB: SlackCloneDB) : ChannelsDataSource {
  override fun getChannels(workspaceId: String): Flow<Query<SkChannel>> {
    return slackCloneDB.slackschemaQueries
      .selectAllChannels(workspaceId)
      .asFlow()
  }

  override fun insertChannel(channel: SkChannel): SkChannel {
    slackCloneDB.slackschemaQueries.insertChannel(
      channel.uuid,
      channel.workspaceId,
      channel.name,
      channel.createdDate,
      channel.modifiedDate,
      channel.isMuted,
      channel.isPrivate,
      channel.isStarred,
      channel.isShareOutSide,
      channel.isOneToOne,
      channel.avatarUrl
    )
    return channel
  }
}
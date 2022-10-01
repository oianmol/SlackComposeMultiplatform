package dev.baseio.slackserver.data

import com.squareup.sqldelight.Query
import database.SkChannel
import kotlinx.coroutines.flow.Flow

interface ChannelsDataSource {
  fun getChannels(workspaceId:String): Flow<Query<SkChannel>>
  abstract fun insertChannel(channel: SkChannel): SkChannel
}
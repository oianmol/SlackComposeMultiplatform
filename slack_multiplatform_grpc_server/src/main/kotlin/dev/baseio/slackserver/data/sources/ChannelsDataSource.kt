package dev.baseio.slackserver.data.sources

import dev.baseio.slackserver.data.models.SkChannel
import dev.baseio.slackserver.data.models.SkChannelMember
import kotlinx.coroutines.flow.Flow

interface ChannelsDataSource {
  fun getChannelChangeStream(workspaceId: String): Flow<Pair<SkChannel.SkGroupChannel?, SkChannel.SkGroupChannel?>>
  fun getDMChannelChangeStream(workspaceId: String): Flow<Pair<SkChannel.SkDMChannel?, SkChannel.SkDMChannel?>>
  suspend fun savePublicChannel(request: SkChannel.SkGroupChannel, adminId: String): SkChannel.SkGroupChannel?
  suspend fun saveDMChannel(request: SkChannel.SkDMChannel): SkChannel.SkDMChannel?
  suspend fun getAllChannels(workspaceId: String, userId: String): List<SkChannel.SkGroupChannel>
  suspend fun getAllDMChannels(workspaceId: String, userId: String): List<SkChannel.SkDMChannel>
  suspend fun checkIfDMChannelExists(userId: String, receiverId: String?):SkChannel.SkDMChannel?
  suspend fun getChannelById(channelId: String, workspaceId: String): SkChannel?

  suspend fun getChannelByName(channelId: String, workspaceId: String): SkChannel?
  fun getChannelMemberChangeStream(
    workspaceId: String,
    memberId: String
  ): Flow<Pair<SkChannelMember?, SkChannelMember?>>

  suspend fun checkIfGroupExisits(workspaceId: String?, name: String?): Boolean
}


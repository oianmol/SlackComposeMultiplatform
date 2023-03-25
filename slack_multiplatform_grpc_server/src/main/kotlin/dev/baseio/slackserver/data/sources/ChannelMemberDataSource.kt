package dev.baseio.slackserver.data.sources

import dev.baseio.slackserver.data.models.SkChannel
import dev.baseio.slackserver.data.models.SkChannelMember

interface ChannelMemberDataSource {
  suspend fun isChannelExistFor(sender: String, receiver: String): SkChannel?
  suspend fun addMembers(listOf: List<SkChannelMember>)
  suspend fun getMembers(workspaceId: String, channelId: String): List<SkChannelMember>
  suspend fun getChannelIdsForUserAndWorkspace(userId: String, workspaceId: String): List<String>
  suspend fun isMember(userId: String, workspaceId: String, channelId: String): Boolean
}